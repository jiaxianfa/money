package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.service.UserService;
import com.bjpn.money.util.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController{
    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 20000)
    LoanInfoService loanInfoService;
    @Reference(interfaceClass = UserService.class,version = "1.0.0",timeout = 20000)
    UserService userService;
   @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;
   //同步操作
    @GetMapping("index")
    public String Index(Model model) {
        //动力金融网历史年化收益率(平均利率)
        Double loanInfoHistoryRateAvg=loanInfoService.queryLoanInfoHistoryRateAvg();
        model.addAttribute(Constant.LOAN_INFO_HISTORY_RATE_AVG, loanInfoHistoryRateAvg);
        //平台用户数
        Long userCount=userService.queryUserCount();
        model.addAttribute(Constant.USER_COUNT, userCount);
        //累计成交额
        Double bidMoneySum = bidInfoService.queryBidMoneySum();
        model.addAttribute(Constant.BID_MONEY_SUM, bidMoneySum);
        //自己写的代码，参数大于等于3个的话，建议用map集合传参
        Map<String, Object> parasMap = new HashMap<>();
        //新手宝   根据产品类型(0)和数量(1)查询产品信息   type在mysql中是关键字
        parasMap.put("ptype", 0);
        parasMap.put("start", 0);
        parasMap.put("content", 1);
        //下划线是为了显著提醒
        List<LoanInfo> loanInfos_X=loanInfoService.queryLoanInfosByTypeAndNumber(parasMap);
        model.addAttribute("loanInfos_X", loanInfos_X);
        //优选宝   根据产品类型(1)和数量(4)查询产品信息   type在mysql中是关键字
        parasMap.put("ptype", 1);
        parasMap.put("start", 0);
        parasMap.put("content", 4);
        //下划线是为了显著提醒
        List<LoanInfo> loanInfos_Y=loanInfoService.queryLoanInfosByTypeAndNumber(parasMap);
        model.addAttribute("loanInfos_Y", loanInfos_Y);
        //散标   根据产品类型(0)和数量(8)查询产品信息   type在mysql中是关键字
        parasMap.put("ptype", 2);
        parasMap.put("start", 0);
        parasMap.put("content", 8);
        //下划线是为了显著提醒
        List<LoanInfo> loanInfos_S=loanInfoService.queryLoanInfosByTypeAndNumber(parasMap);
        model.addAttribute("loanInfos_S", loanInfos_S);
        return "index";
    }
}
