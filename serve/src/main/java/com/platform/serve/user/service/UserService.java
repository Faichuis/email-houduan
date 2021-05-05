package com.platform.serve.user.service;

import com.platform.serve.user.dto.UserInfoDto;

/**
 * 用户信息（注册、登录、忘记密码、注销等）业务处理Service
 *
 * @Author lds
 * @Date 2021/4/12 9:26
 */
public interface UserService {

    /**
     * 注册用户
     *
     * @param userInfoDto
     */
    void userRegister(UserInfoDto userInfoDto);

    /**
     * 用户登录
     *
     * @param userInfoDto
     * @return
     */
    UserInfoDto loginUser(UserInfoDto userInfoDto);

    /**
     * 修改用户信息
     *
     * @param userInfoDto
     */
    void updateUserInfo(UserInfoDto userInfoDto);

    /**
     * 修改用户密码、注册手机号、注册邮箱信息
     *
     * @param userInfoDto
     */
    void updateSafeData(UserInfoDto userInfoDto);


}
