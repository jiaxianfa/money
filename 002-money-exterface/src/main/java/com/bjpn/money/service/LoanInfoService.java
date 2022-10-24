package com.bjpn.money.service;

import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.util.PageModel;

import java.util.List;
import java.util.Map;

/**
 * 产品业务接口
 */
public interface LoanInfoService{
    /**
     * 动力金融网历史年化收益率
     * @return 年化收益率
     */
    Double queryLoanInfoHistoryRateAvg();

    /**
     * 首页：根据产品类型和数量 查询产品信息
     * @param parasMap:pType,start,content
     * @return:产品集合
     */
    List<LoanInfo> queryLoanInfosByTypeAndNumber(Map<String, Object> parasMap);

    /**
     * 列表：根据产品类型和分页模型查询产品信息
     * @param ptype：产品类型
     * @param pageModel：页面对象
     * @return
     */
    List<LoanInfo> queryLoanInfosByTypeAndPageModel(Integer ptype, PageModel pageModel);

    /**
     * 列表：根据产品类型查询总记录数
     * @param ptype
     * @return
     */
    Long queryLoanInfoCountbyType(Integer ptype);

    /**
     *产品详情：根据产品编号，查询产品信息    还需要客户手机号
     * @param loanId
     * @return
     */
    LoanInfo queryLoanInfoById(Integer loanId);
}
