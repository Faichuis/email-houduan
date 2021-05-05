package com.platform.serve.inner.share.controller;

import com.platform.serve.config.result.Result;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import com.platform.serve.inner.share.service.TUserMailboxInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import java.util.List;

/**
 * 用户关联邮箱信息Controller
 *
 * @Author lds
 * @Date 2021/4/13 11:10
 */
@Api(value = "userMail", description = "用户关联邮箱信息")
@RestController
@RequestMapping(path = "/api/userMail")
public class UserMailboxController {

    @Resource
    private TUserMailboxInfoService tUserMailboxInfoService;


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "查询用户关联邮箱", notes = "查询用户关联邮箱接口")
    @PostMapping(value = "/selectMailbox")
    public Result<List<UserMailBoxInfoDto>> selectMailbox(@RequestBody UserMailBoxInfoDto userMailBoxInfoDto) {
        return Result.success("查询用户关联邮箱成功", tUserMailboxInfoService.selectMailbox(userMailBoxInfoDto));
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "用户关联邮箱", notes = "用户关联邮箱接口")
    @PostMapping(value = "/saveMailbox")
    public Result<?> saveMailbox(@RequestBody UserMailBoxInfoDto userMailBoxInfoDto) {
        tUserMailboxInfoService.saveMailbox(userMailBoxInfoDto);
        return Result.success("用户关联邮箱成功");
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "修改用户关联邮箱", notes = "修改用户关联邮箱接口")
    @PostMapping(value = "/updateMailbox")
    public Result<?> updateMailbox(@RequestBody UserMailBoxInfoDto userMailBoxInfoDto) {
        tUserMailboxInfoService.updateMailbox(userMailBoxInfoDto);
        return Result.success("修改用户关联邮箱成功");
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "用户解除关联邮箱", notes = "用户解除关联邮箱接口")
    @PostMapping(value = "/delMailbox")
    public Result<?> delMailbox(@RequestBody UserMailBoxInfoDto userMailBoxInfoDto) {
        tUserMailboxInfoService.delMailbox(userMailBoxInfoDto);
        return Result.success("用户解除关联邮箱成功");
    }


}
