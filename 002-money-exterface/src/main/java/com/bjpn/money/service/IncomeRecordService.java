package com.bjpn.money.service;

import com.bjpn.money.model.IncomeRecord;

import java.util.List;
import java.util.Map;

/**
 * 收益记录接口
 */
public interface IncomeRecordService {
    /**
     * 小金库 最近收益 根据用户id和数量(5)查询投资收益信息
     * @param parasMap
     * @return
     */
    List<IncomeRecord> queryIncomeRecordByUidAndNumber(Map<String, Object> parasMap);
}
