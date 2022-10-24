package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.IncomeRecord;
import com.bjpn.money.model.RechargeRecord;
import com.bjpn.money.model.User;
import com.bjpn.money.service.*;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {
    @Reference(interfaceClass = UserService.class, version = "1.0.0", timeout = 20000)
    UserService userService;
    @Reference(interfaceClass = RedisServer.class, version = "1.0.0", timeout = 20000)
    RedisServer redisServer;
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;
    @Reference(interfaceClass = RechargeRecordService.class,version = "1.0.0",timeout = 20000)
    RechargeRecordService rechargeRecordService;
    @Reference(interfaceClass =IncomeRecordService.class,version = "1.0.0",timeout = 20000)
    IncomeRecordService incomeRecordService;
    //点击注册进入用户注册详情页面
    @GetMapping("/loan/page/register")
    public String register() {
        return "register";
    }

    //验证手机号是否已经被注册过     因为还在验证，不跳转页面，所以用异步操作
    @GetMapping("/loan/page/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(name = "phone", required = true) String phone) {
        //根据手机号码验证  因为牵涉到隐私，所以用 用户数量 代替用户信息
        int num = userService.checkPhone(phone);
        if (num != 0) {
            return Result.error("手机号码已被占用");
        }
        return Result.success();
    }

    //获取验证码     异步操作
    @GetMapping("/loan/page/messageCode")
    @ResponseBody
    public Object messageCode(@RequestParam(name = "phone", required = true) String phone) {
        //生成随机数
        String code = Result.generateCode(6);
        //封装参数
        Map<String, String> parasMap = new HashMap<>();
        //参数名是平台规定好的，除了手机号是前端传参过来的，其他两个都需要参考平台规定
        parasMap.put("mobile", phone);
        parasMap.put("appkey", "a457e90ecc0f5f223e39fa2f71264a96");
        //短信需要经过工信部认真，所以只能先用平台的示例
        parasMap.put("content", "【创信】你的验证码是：" + code + "，3分钟内有效！");
        //调用发送http请求的方法     参数可以直接拼接在地址栏，也可以封装在map集合，单独传参
        String result = "";
        try {
            //result = HttpClientUtils.doGet("https://way.jd.com/chuangxin/dxjk", parasMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统异常");
        }
        //根据平台定义的信息参考key值  不真实发送短信的话，就用这个返回值代替,如果真实发送短信的话，就用真实的result，这个result可以注释掉
        result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"ReturnStatus\": \"Success\",\n" +
                "        \"Message\": \"ok\",\n" +
                "        \"RemainPoint\": 420842,\n" +
                "        \"TaskID\": 18424321,\n" +
                "        \"SuccessCounts\": 1\n" +
                "    }\n" +
                "}";
        //将获取到的结果字符串解析成json对象     fastJson的功能
        JSONObject jsonObject = JSONObject.parseObject(result);
        //获取json对象中的值
        //状态码   字符串
        String statusCode = jsonObject.getString("code");
        if (!StringUtils.equals("10000", statusCode)) {
            return Result.error("通讯异常");
        }
        //数据查询结果    json对象
        JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
        //"result"中的Message会显示没有发送成功的具体信息，如果成功就返回OK
        String returnStatus = jsonObjectResult.getString("ReturnStatus");
        if (!StringUtils.equals("Success", returnStatus)) {
            return Result.error("短信发送失败");
        }
        //发送成功：存放验证码    需要和客户前端输入的值进行校验，所以要从后端发起验证码请求，并存入redis中 存入的key必须唯一，手机号正好满足
        redisServer.push(phone, code);
        //返回操作状态
        return Result.success(code);
    }

    //注册服务器     异步操作    有密码存在，所以需要是post请求
    @PostMapping("/loan/page/registerSubmit")
    @ResponseBody
    public Object registerSubmit(@RequestParam(name = "phone", required = true) String phone,
                                 @RequestParam(name = "loginPassword", required = true) String loginPassword,
                                 @RequestParam(name = "messageCode", required = true) String messageCode,
                                 HttpServletRequest request) {
        //校验：获取redis缓存中的验证码     验证码需要在后台验证  后台方便解析收到的数据
        String code = redisServer.pop(phone);
        if (!StringUtils.equals(code, messageCode)) {
            return Result.error("验证码输入有误");
        }
        //验证没问题 大部分都是通过客户端验证，降低服务器压力    验证码
        // 注册新用户 返回user对象    缺少手机号已经注册过的执行程序(前端页面没有完全限制住)
        User user = userService.register(phone, loginPassword);
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("注册失败");
        }
        //将用户身份存入session中
        request.getSession().setAttribute(Constant.LOGIN_USER, user);
        return Result.success();
    }

    //注册成功以后进入实名认证
    @GetMapping("/loan/page/realName")
    public String realName(String ReturnUrl,Model model) {
        //将当前页的参数存入model域对象中
        model.addAttribute("ReturnUrl", ReturnUrl);
        System.out.println(ReturnUrl);
        return "realName";
    }

    //实名认证  异步操作
    @GetMapping("/loan/page/authentication")
    @ResponseBody
    public Object authentication(@RequestParam(name = "realName", required = true) String realName,
                                 @RequestParam(name = "idCard", required = true) String idCard,
                                 @RequestParam(name = "phone", required = true) String phone,
                                 @RequestParam(name="messageCode",required = true)String messageCode,
                                 HttpServletRequest request) {
        //判断是否已经登录
        User user = (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("请登录后再做实名认证");
        }
        //校验验证码
        //获取之前存入的验证码
        String code = redisServer.pop(phone);
        //判断
        if (!StringUtils.equals(code, messageCode)) {
            return Result.error("验证码错误，请重新输入");
        }
        //封装发送http请求的参数
        Map<String, String> parasMap = new HashMap<>();
        parasMap.put("appkey", "a457e90ecc0f5f223e39fa2f71264a96");
        parasMap.put("cardno", idCard);
        parasMap.put("name", realName);
        String result = "";
        try {
            //result = HttpClientUtils.doGet("https://way.jd.com/yingyan/idcard", parasMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //模拟认证成功
        result="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"resp\": {\n" +
                "            \"code\": 0,\n" +
                "            \"desc\": \"匹配\"\n" +
                "        },\n" +
                "        \"data\": {\n" +
                "            \"birthday\": \"1981-11-30\",\n" +
                "            \"sex\": \"M\",\n" +
                "            \"address\": \"山东省潍坊市寒亭区\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        //将字符串解析成json对象
        JSONObject jsonObject = JSONObject.parseObject(result);
        //获取返回值的状态码
        String statusCode = jsonObject.getString("code");
        //System.out.println(statusCode);
        if (!StringUtils.equals("10000", statusCode)) {
            return Result.error("系统异常，请稍后重试");
        }
        //获得结果集 json对象
        JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
        //获得信息对象
        /*JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
        System.out.println(jsonObjectData.getString("birthday"));
        System.out.println(jsonObjectData.getString("sex"));
        System.out.println(jsonObjectData.getString("address"));*/
        //获得状态对象
        JSONObject jsonObjectResp = jsonObjectResult.getJSONObject("resp");
        //获得结果集的信息
        String message = jsonObjectResp.getString("desc");
        if (!StringUtils.equals("匹配", message)) {
            return Result.error("身份证和姓名不匹配");
        }
        //认证成功  更新用户信息  不一定能够更新成功，所以要创建个新用户，等确认更新成功后，再重新给user对象赋值
        User user_A = new User();
        user_A.setIdCard(idCard);
        user_A.setName(realName);
        user_A.setId(user.getId());

        int num = userService.authentication(user_A);
        if (num!=1) {
            return Result.error("实名认证失败");
        }
        //认证成功  更新session用户对象信息
        user.setIdCard(idCard);
        user.setName(realName);

        return Result.success();
    }
   /*认证成功以后进入登录页面   同步操作
    用户有可能从任意页面登录，成功以后需要返回到当前页面，所以点击登录的时候需要传入当前的地址
    因为每个页面都需要显示登录，所以参数要在公共页面获取*/
    //任意页面进入登录页面    需要页面跳转  同步操作
    @GetMapping("/loan/page/login")
    public String login(String ReturnUrl, Model model) {
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "login";
    }

    //真实登录    异步操作
    @PostMapping("/loan/page/loginSubmit")
    @ResponseBody
    public Object loginSubmit(@RequestParam(name = "phone", required = true) String phone, @RequestParam(name = "loginPassword", required = true) String loginPassword,HttpServletRequest request,HttpServletResponse response) {
        //根据手机号和密码查询用户信息    也可以根据用户名查询，效率高，但是互联网项目不安全
        User user = userService.login(phone, loginPassword);
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("用户名或密码不匹配");
        }
        //将用户信息存入缓存中
        request.getSession().setAttribute(Constant.LOGIN_USER, user);
        //记住密码
        Cookie cookieCode = new Cookie("phone", phone);
        Cookie cookiePwd = new Cookie("loginPassword", loginPassword);
        //账号保存10天
        cookieCode.setPath("/");
        cookieCode.setMaxAge(60 * 60 * 24 * 10);
        //密码保存10天
        cookiePwd.setPath("/");
        cookiePwd.setMaxAge(60 * 60 * 24 * 10);
        response.addCookie(cookieCode);
        response.addCookie(cookiePwd);
//        System.out.println("cookieCode="+cookieCode);
//        System.out.println("cookiePwd="+cookiePwd);
        return Result.success();
    }

    //进入小金库     同步操作
    @GetMapping("/loan/page/myCenter")
    public String toMyCenter(HttpServletRequest request,Model model) {
        //验证是否已经登录
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if (ObjectUtils.allNotNull(user)) {
            //最近投资  根据用户id和数量(5)查询投资产品信息
            Map<String, Object> parasMap = new HashMap<>();
            parasMap.put("userId", user.getId());
            parasMap.put("start", 0);
            parasMap.put("content", 5);
            List<BidInfo> bidInfos = bidInfoService.queryBidInfoByUidAndNumber(parasMap);
            model.addAttribute("bidInfos", bidInfos);
            //最近充值  根据用户id和数量(5)查询投资记录信息
            List<RechargeRecord> rechargeRecords = rechargeRecordService.queryRechargeRecordByUidAndNumber(parasMap);
            model.addAttribute("rechargeRecords", rechargeRecords);
            //最近收益 根据用户id和数量(5)查询投资收益信息
            List<IncomeRecord> incomeRecords=incomeRecordService.queryIncomeRecordByUidAndNumber(parasMap);
            model.addAttribute("incomeRecords",incomeRecords);
        }
        return "myCenter";
    }

    //文件上传
    @PostMapping("/loan/uploadHeader")
    @ResponseBody
    public Object uploadHeader(MultipartFile header, HttpServletRequest request) {
        //验证是否已经登录
        User user = (User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("请登录后再更改头像");
        }
        //获得文件全名
        String originalFilename = header.getOriginalFilename();
        //获得文件类型
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成新的文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + fileType;
        //将文件放到header文件夹里面      这里的文件输出路径需要有问题，所以写全路径
        File file=new File("E:\\bjpn\\myselfCode\\SH2207_idea_javaee\\money\\005-money-web\\src\\main\\resources\\static\\header\\"+newFileName);
        //上传文件
        try {
            header.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
        //创建新的user对象更新数据库
        User user_H = new User();
        user_H.setHeaderImage(newFileName);
        user_H.setId(user.getId());
        //通过用户id更改文件头像路径
        int num = userService.updateHeaderByUserId(user_H);
        if (num == 0) {
            return Result.error("更新头像失败");
        }
        //更新session中user的头像路径
        user.setHeaderImage(newFileName);
        return Result.success();

    }

    //退出登录  还需要停留在当前页面，所以是异步
    @GetMapping("/loan/logout")
    @ResponseBody
    public Object logout(HttpServletRequest request,HttpServletResponse response) {
        // 1. 销毁Session对象
        request.getSession().invalidate();
        // 2. 删除Cookie对象
        Cookie cookieCode = new Cookie("phone", null);
        Cookie cookiePwd = new Cookie("loginPassword", null);
        //账号密码生命周期设为0
        cookieCode.setMaxAge(0);
        cookiePwd.setMaxAge(0);
        response.addCookie(cookieCode);
        response.addCookie(cookiePwd);
        return Result.success("退出成功，欢迎下次再来！！！");
    }
}
