package com.bjpn.money.service;

/**
 * Redis服务接口
 */
public interface RedisServer {
    /**
     * 注册：存放验证码
     * @param phone
     * @param code
     */
    void push(String phone, String code);

    /**
     * 注册：获取验证码
     * @param phone
     * @return
     */
    String pop(String phone);
}
