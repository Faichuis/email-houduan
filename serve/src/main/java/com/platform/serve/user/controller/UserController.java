package com.platform.serve.user.controller;

import com.platform.serve.config.result.Result;
import com.platform.serve.user.dto.UserInfoDto;
import com.platform.serve.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户信息管理
 *
 * @Author lds
 * @Date 2021/4/12 9:23
 */
@Api(value = "user", description = "用户信息类")
@RestController
@RequestMapping(path = "/login/user")
public class UserController {

    @Resource
    private UserService userService;


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @PostMapping(value = "/loginUser")
    public Result<UserInfoDto> loginUser(@RequestBody UserInfoDto userInfoDto) {
        UserInfoDto userInfoDto1 = userService.loginUser(userInfoDto);
        return Result.success("用户登录成功！", userInfoDto1);
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "注册用户", notes = "注册用户接口")
    @PostMapping(value = "/register")
    public Result<Object> userRegister(@RequestBody UserInfoDto userInfoDto) {
        userService.userRegister(userInfoDto);
        return Result.success("注册用户成功！");
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息接口")
    @PostMapping(value = "/updateUserInfo")
    public Result<Object> updateUserInfo(@RequestBody UserInfoDto userInfoDto) {
        userService.updateUserInfo(userInfoDto);
        return Result.success("修改用户信息成功！");
    }

    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "修改用户密码、注册手机号、注册邮箱信息", notes = "修改用户密码、注册手机号、注册邮箱信息接口")
    @PostMapping(value = "/updateSafeData")
    public Result<Object> updateSafeData(@RequestBody UserInfoDto userInfoDto) {
        userService.updateSafeData(userInfoDto);
        return Result.success("修改信息成功！");
    }

}
