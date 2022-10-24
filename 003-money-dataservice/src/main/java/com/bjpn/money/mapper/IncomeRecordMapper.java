package com.bjpn.money.mapper;

import com.bjpn.money.model.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);
    //小金库 最近收益 根据用户id和数量(5)查询投资收益信息
    List<IncomeRecord> selectIncomeRecordByUidAndNumber(Map<String, Object> parasMap);
}