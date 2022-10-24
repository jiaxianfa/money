package com.bjpn.money.service;


import com.bjpn.money.model.BidInfo;

import java.util.List;
import java.util.Map;

/**
 * 投资业务接口
 */
public interface BidInfoService {
    /**
     * 首页：累计成交额
     * @return 成交额
     */
    Double queryBidMoneySum();

    /**
     * 产品详情页：根据id查询投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> queryBidInfosByLoanId(Integer loanId);

    /**
     * 小金库：根据用户id和数量查询投资信息
     * @param parasMap
     * @return
     */

    List<BidInfo> queryBidInfoByUidAndNumber(Map<String, Object> parasMap);

    /**
     * 投资：根据产品id，用户id，投资金额进行投资
     * @param parasMap
     * @return
     */
    int invest(Map<String, Object> parasMap);
}
