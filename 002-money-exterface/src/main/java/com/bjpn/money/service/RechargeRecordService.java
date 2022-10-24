package com.bjpn.money.service;

import com.bjpn.money.model.RechargeRecord;

import java.util.List;
import java.util.Map;

/**
 * 用户充值记录接口
 */
public interface RechargeRecordService {
    /**
     * 小金库  最近充值  根据用户id和数量(5)查询投资信息
     * @param parasMap
     * @return
     */
    List<RechargeRecord> queryRechargeRecordByUidAndNumber(Map<String, Object> parasMap);
}
