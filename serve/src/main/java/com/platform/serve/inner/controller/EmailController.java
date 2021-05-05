package com.platform.serve.inner.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.serve.aspect.RunTime;
import com.platform.serve.config.result.Result;
import com.platform.serve.inner.dto.EmailInfoDto;
import com.platform.serve.inner.service.EmailManageService;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import com.platform.serve.inner.service.ReadEmailService;
import com.platform.serve.inner.service.SendEmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * email业务类
 *
 * @author lids
 * @version 1.0
 * @date 2020/12/20 21:26
 */
@Api(value = "email", description = "email业务类")
@RestController
@RequestMapping(path = "/api/email")
public class EmailController {

    @Resource
    private SendEmailService sendEmailService;
    @Resource
    private ReadEmailService readEmailService;
    @Resource
    private EmailManageService emailManageService;


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "判断账号是否可用", notes = "判断账号是否可用接口")
    @PostMapping(value = "/judgeMailUser")
    @RunTime
    public Result<Boolean> judgeMailUserIsNormal(@RequestBody UserMailBoxInfoDto userMailBoxInfoDto) {
        return Result.success("判断账号是否可用成功！", readEmailService.judgeMailUserIsNormal(userMailBoxInfoDto));
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "接收Email", notes = "接收Email接口")
    @PostMapping(value = "/readEmail")
    public Result readEmail(@RequestBody UserMailBoxInfoDto userMailBoxInfoDto) {
        readEmailService.readEmail(userMailBoxInfoDto);
        return Result.success("接收Email成功！");
    }

    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "接收用户所有的Email", notes = "接收用户所有的Email接口")
    @PostMapping(value = "/readEmailByUserCode")
    public Result readEmailByUserCode(@RequestBody UserMailBoxInfoDto userMailBoxInfoDto) {
        readEmailService.readEmailByUserCode(userMailBoxInfoDto);
        return Result.success("接收用户的Email成功！");
    }

    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "发送Email", notes = "发送Email接口")
    @PostMapping(value = "/sendEmail")
    public Result sendEmail(@RequestBody EmailInfoDto emailInfoDto) {
        sendEmailService.sendEmailMessage(emailInfoDto);
        return Result.success("发送Email成功！");
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "查询发件箱集合", notes = "查询发件箱集合接口")
    @PostMapping(value = "/selectOutboxEmailList")
    public Result<Page<EmailInfoDto>> selectOutboxEmailList(@RequestBody EmailInfoDto emailInfoDto) {
        return Result.success("查询发件箱集合成功！", emailManageService.selectOutboxEmailList(emailInfoDto));
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "查询收件箱集合", notes = "查询收件箱集合接口")
    @PostMapping(value = "/selectInboxEmailList")
    public Result<Page<EmailInfoDto>> selectInboxEmailList(@RequestBody EmailInfoDto emailInfoDto) {
        return Result.success("查询收件箱集合成功！", emailManageService.selectInboxEmailList(emailInfoDto));
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "查询发件箱详细信息", notes = "查询发件箱详细信息接口")
    @PostMapping(value = "/selectOutboxInfo")
    public Result<EmailInfoDto> selectOutboxInfo(@RequestBody EmailInfoDto emailInfoDto) {
        return Result.success("查询发件箱详细信息成功！", emailManageService.selectOutboxInfo(emailInfoDto));
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "查询收件箱详细信息", notes = "查询收件箱详细信息接口")
    @PostMapping(value = "/selectInboxInfo")
    public Result<EmailInfoDto> selectInboxInfo(@RequestBody EmailInfoDto emailInfoDto) {
        return Result.success("查询收件箱详细信息成功！", emailManageService.selectInboxInfo(emailInfoDto));
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "删除邮件", notes = "删除邮件接口")
    @PostMapping(value = "/delEmail")
    public Result delEmail(@RequestBody EmailInfoDto emailInfoDto) {
        emailManageService.delEmail(emailInfoDto);
        return Result.success("删除邮件成功！");
    }


}
