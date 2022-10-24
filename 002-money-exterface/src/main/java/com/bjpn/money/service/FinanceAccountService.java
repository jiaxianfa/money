package com.bjpn.money.service;

import com.bjpn.money.model.FinanceAccount;

/**
 * 账户业务接口
 */
public interface FinanceAccountService {
    /**
     * 用户信息下拉列表：登录成功以后，根据用户id查询账户信息
     * @param userId
     * @return
     */
    FinanceAccount queryFinanceByUserId(Integer userId);
}

