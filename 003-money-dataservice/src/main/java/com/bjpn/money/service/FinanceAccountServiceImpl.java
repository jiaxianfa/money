package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.model.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账户业务实现类
 */
@Service(interfaceClass = FinanceAccountService.class,version = "1.0.0",timeout = 20000)
@Component
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Autowired
    FinanceAccountMapper financeAccountMapper;
    //用户信息下拉列表：登录成功以后，根据用户id查询账户信息
    @Override
    public FinanceAccount queryFinanceByUserId(Integer userId) {
        return financeAccountMapper.selectFinanceByUserId(userId);
    }
}
