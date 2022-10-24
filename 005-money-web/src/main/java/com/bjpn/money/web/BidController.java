package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.User;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BidController {
    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", timeout = 20000)
    LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;
    @PostMapping("/loan/page/invest")
    //投资处理器
    @ResponseBody
    public Object invest(@RequestParam(name = "loanId", required = true) Integer loanId, @RequestParam(name = "bidMoney", required = false) Double bidMoney, HttpServletRequest request) {
        //验证：
        //是否登陆
        User user = (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        if (!ObjectUtils.anyNotNull(user)) {
            return "请登录后再进行投资";
        }
        //是否实名认证
        if (!ObjectUtils.allNotNull(user.getName(),user.getIdCard())) {
            return "对不起，实名认证以后才能进行投资";
        }
        //基础验证
        //投资金额不能为空
        if (bidMoney==null||bidMoney==0) {
            return "对不起,投资金额不能为空";
        }
        //产品id不能为空
        if (loanId == null || loanId == 0) {
            return "请选择要投资的产品";
        }
        //必须是数字     能传到后端的一定是数字
        //投资金额必须>0
        if (bidMoney <0) {
            return "您好，投资金额必须大于0";
        }
        //投资金额必须是100的整数倍  bidMoney%100=0说明是100的整数倍
        if (bidMoney % 100 > 0) {
            return "您好，投资金额必须是100的整数倍";
        }
        //业务验证
        //根据产品id查询产品信息
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(loanId);
        //投资金额的限制
        if (bidMoney<loanInfo.getBidMinLimit()||bidMoney>loanInfo.getBidMaxLimit()) {
            return "投资金额必须在"+loanInfo.getBidMinLimit()+"和"+loanInfo.getBidMaxLimit()+"之间";
        }
        if (bidMoney>loanInfo.getLeftProductMoney()) {
            return "投资金额必须在"+loanInfo.getBidMinLimit()+"和"+loanInfo.getLeftProductMoney()+"之间";
        }
        //开始投资
        //参数超过2个，封装成map集合
        Map<String,Object> parasMap = new HashMap<>();
        parasMap.put("userId", user.getId());
        parasMap.put("loanId", loanId);
        parasMap.put("bidMoney", bidMoney);
        int statusCode=0;
        //捕捉异常信息
        try {
            //以后如果不知道返回值类型，可以先写String
            statusCode=bidInfoService.invest(parasMap);
        } catch (Exception e) {
            e.printStackTrace();
            return "系统异常，请稍后再试";
        }
        return statusCode;
    }
}
