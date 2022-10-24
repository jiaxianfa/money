package com.bjpn.money.mapper;

import com.bjpn.money.model.RechargeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);
    //小金库  最近充值  根据用户id和数量(5)查询投资信息
    List<RechargeRecord> selectRechargeRecordByUidAndNumber(Map<String, Object> parasMap);
}