package com.bjpn.money.service;


import com.bjpn.money.model.User;

/**
 * 用户业务接口
 */
public interface UserService{
    /**
     * 首页：平台用户数
     * @return 用户数量
     */
    Long queryUserCount();

    /**
     * 注册：验证手机号
     * @param phone
     * @return
     */
    int checkPhone(String phone);

    /**
     *注册：用户注册
     * @param phone
     * @param loginPassword
     * @return
     */
    User register(String phone, String loginPassword);

    /**
     * 认证：认证成功以后更新用户身份证和姓名
     * @param user_a
     * @return
     */
    int authentication(User user_a);

    /**
     * 登录：根据手机号和密码查询用户
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword);

    /**
     * 小金库：通过用户id更改文件头像路径
     * @param user
     * @return
     */
    int updateHeaderByUserId(User user);
}
