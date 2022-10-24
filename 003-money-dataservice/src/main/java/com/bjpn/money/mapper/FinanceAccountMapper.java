package com.bjpn.money.mapper;

import com.bjpn.money.model.FinanceAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);
    //用户信息下拉列表：登录成功以后，根据用户id查询账户信息
    FinanceAccount selectFinanceByUserId(Integer userId);
    //投资：减少账户可用余额
    int updateAvailableMoneyReduceForInvest(Map<String, Object> parasMap);
}