package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.util.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class LoanController {
    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 20000)
    LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;
    //产品列表页面    同步操作    a标签
    @GetMapping("/loan/loan")
    //这里的当前页就是客户想跳转的页面了，已经在前端处理过当前页的数据了，后面的方法是在后端处理当前页
    public String loan(@RequestParam(name="ptype",required = true)Integer ptype, Long cunPage, Model model, HttpServletRequest request) {
        //从前端获得pageModel对象
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null) {
            //设置pageModel对象
            pageModel=new PageModel(9);
            cunPage=pageModel.getFirstPage().longValue();
            //pageModel.setLastPage();
            //将对象存入session中
            request.getSession().setAttribute("pageModel", pageModel);
        }
        //查询总记录数
        Long totalCount = loanInfoService.queryLoanInfoCountbyType(ptype);
        pageModel.setTotalCount(totalCount);
        //设置当前页     需要判断地址栏的请求
        if (null==cunPage || cunPage<1) {
            //默认是首页
            cunPage = pageModel.getFirstPage().longValue();
        }
        if (cunPage > pageModel.getLastPage()) {
            cunPage=pageModel.getLastPage();
        }
        //将前端的当前页存入对象中
        pageModel.setCunPage(cunPage);
        //展现产品  根据产品类型和页面对象查询产品
        List<LoanInfo> loanInfos = loanInfoService.queryLoanInfosByTypeAndPageModel(ptype, pageModel);
        model.addAttribute("loanInfos", loanInfos);
        model.addAttribute("ptype", ptype);
        //todo:投资排行榜
        return "loan";
    }
    //产品详情页面    同步操作    a标签
    @GetMapping("/loan/loanInfo")
    public String loanInfo(@RequestParam(name = "loanId",required = true)Integer loanId,Model model,HttpServletRequest request) {
        //产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(loanId);
        model.addAttribute("loanInfo", loanInfo);

        //投资记录 还需要客户手机号
        List<BidInfo> bidInfos = bidInfoService.queryBidInfosByLoanId(loanId);
        model.addAttribute("bidInfos", bidInfos);
        return "loanInfo";
    }
    //产品列表第一页
    /*@GetMapping("/loan/loan/doFirst")
    public String  doFirst(@RequestParam(name="ptype",required = true)Integer ptype, Long cunPage,Model model, HttpServletRequest request) {
        //从前端获得pageModel对象
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null) {
            //设置pageModel对象
            pageModel=new PageModel(9);
            cunPage=pageModel.getFirstPage().longValue();
            //pageModel.setLastPage();
            //将对象存入session中
            request.getSession().setAttribute("pageModel", pageModel);
        }
        //查询总记录数    分页需要
        Long totalCount = loanInfoService.queryLoanInfoCountbyType(ptype);
        pageModel.setTotalCount(totalCount);
        //设置当前页     需要判断地址栏的请求
        if (null==cunPage || cunPage<1||cunPage > pageModel.getLastPage()) {
            //全部设置为首页
            cunPage = pageModel.getFirstPage().longValue();
        }
        pageModel.setCunPage(1l);
        //展现产品  根据产品类型和页面对象查询产品
        List<LoanInfo> loanInfos = loanInfoService.queryLoanInfosByTypeAndPageModel(ptype, pageModel);
        model.addAttribute("loanInfos", loanInfos);
        model.addAttribute("ptype", ptype);
        //todo:投资排行榜
        return "loan";
    }*/
    //产品列表上一页
   /* @GetMapping("/loan/loan/doUp")
    public String doUp(@RequestParam(name="ptype",required = true)Integer ptype, Long cunPage, Model model, HttpServletRequest request) {
        //从前端获得pageModel对象
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null) {
            //设置pageModel对象
            pageModel=new PageModel(9);
            cunPage=pageModel.getFirstPage().longValue();
            //pageModel.setLastPage();
            //将对象存入session中
            request.getSession().setAttribute("pageModel", pageModel);
        }
        //查询总记录数
        Long totalCount = loanInfoService.queryLoanInfoCountbyType(ptype);
        pageModel.setTotalCount(totalCount);
        //设置当前页     需要判断地址栏的请求
        if (null==cunPage || cunPage<1) {
            //默认是首页
            cunPage = pageModel.getFirstPage().longValue();
        }
        if (cunPage > pageModel.getLastPage()) {
            cunPage=pageModel.getLastPage();
        }
        cunPage--;
        //将前端的当前页存入对象中
        pageModel.setCunPage(cunPage);
        //展现产品  根据产品类型和页面对象查询产品
        List<LoanInfo> loanInfos = loanInfoService.queryLoanInfosByTypeAndPageModel(ptype, pageModel);
        model.addAttribute("loanInfos", loanInfos);
        model.addAttribute("ptype", ptype);
        //todo:投资排行榜
        return "loan";
    }*/
    //产品列表尾页
    /*@GetMapping("/loan/loan/doLast")
    public String doLast(@RequestParam(name="ptype",required = true)Integer ptype, Long cunPage, Model model, HttpServletRequest request) {
        //从前端获得pageModel对象
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null) {
            //设置pageModel对象
            pageModel=new PageModel(9);
            cunPage=pageModel.getLastPage();
            //pageModel.setLastPage();
            //将对象存入session中
            request.getSession().setAttribute("pageModel", pageModel);
        }
        //查询总记录数
        Long totalCount = loanInfoService.queryLoanInfoCountbyType(ptype);
        pageModel.setTotalCount(totalCount);
        //全部设置为尾页
        if (null==cunPage || cunPage<1||cunPage > pageModel.getLastPage()) {
            cunPage=pageModel.getLastPage();
        }
        //将前端的当前页存入对象中
        pageModel.setCunPage(pageModel.getLastPage());
        //展现产品  根据产品类型和页面对象查询产品
        List<LoanInfo> loanInfos = loanInfoService.queryLoanInfosByTypeAndPageModel(ptype, pageModel);
        model.addAttribute("loanInfos", loanInfos);
        model.addAttribute("ptype", ptype);
        //todo:投资排行榜
        return "loan";
    }*/
    //产品列表第几页
    /*@GetMapping("/loan/loan/doGo")
    public String doGo(@RequestParam(name="ptype",required = true)Integer ptype, Long cunPage, Model model, HttpServletRequest request) {
        //从前端获得pageModel对象
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null) {
            //设置pageModel对象
            pageModel=new PageModel(9);
            cunPage=pageModel.getFirstPage().longValue();
            //pageModel.setLastPage();
            //将对象存入session中
            request.getSession().setAttribute("pageModel", pageModel);
        }
        //查询总记录数
        Long totalCount = loanInfoService.queryLoanInfoCountbyType(ptype);
        pageModel.setTotalCount(totalCount);
        //设置当前页     需要判断地址栏的请求
        if (null==cunPage || cunPage<1) {
            //默认是首页
            cunPage = pageModel.getFirstPage().longValue();
        }
        if (cunPage > pageModel.getLastPage()) {
            cunPage=pageModel.getLastPage();
        }
        //将前端的当前页存入对象中
        pageModel.setCunPage(cunPage);
        //展现产品  根据产品类型和页面对象查询产品
        List<LoanInfo> loanInfos = loanInfoService.queryLoanInfosByTypeAndPageModel(ptype, pageModel);
        model.addAttribute("loanInfos", loanInfos);
        model.addAttribute("ptype", ptype);
        //todo:投资排行榜
        return "loan";
    }*/
}
