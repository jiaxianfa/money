package com.bjpn.money.mapper;

import com.bjpn.money.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    //非空更改  如果是传进来的是空值，就不更改
    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //首页：平台用户数量
    Long selectUserCount();

    //注册：验证手机号码
    int selectUserCountByPhone(String phone);

    //登录：根据手机号和密码查询用户   这里传2个参数，根mybatis的版本有关系，不能用的话，就传map集合
    User selectUserByPhoneAndPassword(String phone, String loginPassword);
}