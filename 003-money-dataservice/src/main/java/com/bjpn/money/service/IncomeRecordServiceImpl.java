package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.IncomeRecordMapper;
import com.bjpn.money.model.IncomeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
/**
 * 收益记录实现类
 */
@Service(interfaceClass =IncomeRecordService.class,version = "1.0.0",timeout = 20000)
@Component
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    IncomeRecordMapper incomeRecordMapper;
    //小金库 最近收益 根据用户id和数量(5)查询投资收益信息
    @Override
    public List<IncomeRecord> queryIncomeRecordByUidAndNumber(Map<String, Object> parasMap) {
        return incomeRecordMapper.selectIncomeRecordByUidAndNumber(parasMap);
    }

}
