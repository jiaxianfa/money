package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * Redis服务实现类
 */
@Service(interfaceClass = RedisServer.class, version = "1.0.0", timeout = 20000)
//为了不和dubbo的service冲突，所以用Component注解
@Component
public class RedisServerImpl implements RedisServer {
    @Autowired(required = false)
    RedisTemplate redisTemplate;
    //向redis中存入验证码，等待验证客户端输入的值
    @Override
    public void push(String phone, String code) {
        //保存5分钟     没有返回值，说明程序肯定不会出错
        redisTemplate.opsForValue().set(phone, code,5, TimeUnit.MINUTES);
    }
    //获取缓存中的验证码
    @Override
    public String pop(String phone) {
        return (String)redisTemplate.opsForValue().get(phone);
    }
}
