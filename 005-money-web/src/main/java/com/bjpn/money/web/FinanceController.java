package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.User;
import com.bjpn.money.service.FinanceAccountService;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FinanceController {
    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",timeout = 20000)
    FinanceAccountService financeAccountService;
    //实时更新账户余额  异步操作
    @GetMapping("/loan/page/queryFinance")
    @ResponseBody
    public Object queryFinance(HttpServletRequest request) {
        //验证是否已经登录
       User user= (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("请先登录后再查询账户信息");
        }
        //查询账户信息
        FinanceAccount financeAccount=financeAccountService.queryFinanceByUserId(user.getId());
        if (!ObjectUtils.allNotNull(financeAccount)) {
            return Result.error("服务器繁忙，请稍后再查询...");
        }
        //request.getSession().setAttribute("financeAccount", financeAccount);
        //因为是ajax，所以不能用域对象了
        return Result.success(financeAccount.getAvailableMoney()+"");
    }
}
