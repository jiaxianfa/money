package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.RechargeRecordMapper;
import com.bjpn.money.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 用户充值记录实现类
 */
@Service(interfaceClass = RechargeRecordService.class,version = "1.0.0",timeout = 20000)
@Component
public class RechargeRecordServiceImpl implements RechargeRecordService {
    @Autowired
    RechargeRecordMapper rechargeRecordMapper;
    //小金库  最近充值  根据用户id和数量(5)查询投资信息
    @Override
    public List<RechargeRecord> queryRechargeRecordByUidAndNumber(Map<String, Object> parasMap) {
        return rechargeRecordMapper.selectRechargeRecordByUidAndNumber(parasMap);
    }
}
