package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.UserMapper;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.User;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.ThreadPoolUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用户业务实现类
 */
@Service(interfaceClass = UserService.class,version = "1.0.0",timeout = 20000)
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired(required = false)
    RedisTemplate redisTemplate;
    @Autowired
    FinanceAccountMapper financeAccountMapper;
    //首页：平台用户数量
    @Override
    public Long queryUserCount() {
        Long userCount = (Long) redisTemplate.opsForValue().get(Constant.USER_COUNT);
        //如果不为空，可以直接返回值，不需要多线程了，效率更高
        if (null == userCount) {
            synchronized (this) {
                //因为多线程，需要再确认一下
                userCount = (Long) redisTemplate.opsForValue().get(Constant.USER_COUNT);
                if (null==userCount) {
                    //从数据库中查询
                    userCount = userMapper.selectUserCount();
                    //将查到的值添加到redis中
                    redisTemplate.opsForValue().set(Constant.USER_COUNT, userCount);
                }
            }
        }
        return userCount;
    }
    //注册：验证手机号
    //注册和大礼包不应该加事务，不应该有影响
    @Override
    public int checkPhone(String phone) {
        return userMapper.selectUserCountByPhone(phone);
    }
    //注册：用户注册
    @Override
    public User register(String phone, String loginPassword) {
        //controller只负责传参数，这里new对象
        User user = new User();
        //添加注册时间
        user.setAddTime(new Date());
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        //直接调用mybatis自动生成的方法
        int num = userMapper.insertSelective(user);
        if (num==1) {
            //注册成功，需要发放大礼包，但是为了不影响注册效率，需要加上返还大礼包线程
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    //将信息存入用户资金财产账户表
                    FinanceAccount financeAccount = new FinanceAccount();
                    financeAccount.setAvailableMoney(888d);
                    //想要sql执行结束后返回id的话，需要在xml里面设置一下
                    financeAccount.setUid(user.getId());
                    financeAccountMapper.insertSelective(financeAccount);
                }
            }).start();
           */
            ThreadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {
                    //将信息存入用户资金财产账户表
                    FinanceAccount financeAccount = new FinanceAccount();
                    financeAccount.setAvailableMoney(888d);
                    //想要sql执行结束后返回id的话，需要在xml里面设置一下
                    financeAccount.setUid(user.getId());
                    financeAccountMapper.insertSelective(financeAccount);
                }
            });
            //正常的注册线程
            return user;
        }
        return null;
    }
    //认证：认证成功以后更新用户身份证和姓名
    @Override
    public int authentication(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }
    //登录：根据手机号和密码查询用户
    @Override
    public User login(String phone, String loginPassword) {

        User user = userMapper.selectUserByPhoneAndPassword(phone, loginPassword);
        if (ObjectUtils.allNotNull(user)) {
            //登录成功，需要添加最近登录的时间，为了不影响登录，使用其他线程
            ThreadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {
                    user.setLastLoginTime(new Date());
                    userMapper.updateByPrimaryKeySelective(user);
                }
            });
        }
        return user;
    }
    //小金库：通过用户id更改文件头像路径
    @Override
    public int updateHeaderByUserId(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }
}
