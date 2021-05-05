package com.platform.serve.inner.templatemail.controller;

import com.platform.serve.config.result.Result;
import com.platform.serve.inner.dto.FileInfoDto;
import com.platform.serve.inner.templatemail.service.VerifyTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 验证邮件信息简单模板
 *
 * @Author lds
 * @Date 2021/4/12 16:59
 */
@Api(value = "template", description = "附件相关业务类")
@RestController
@RequestMapping(path = "/api/template")
public class VerifyTemplateController {

    @Resource
    private VerifyTemplateService verifyTemplateService;


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "验证注册邮箱", notes = "验证注册邮箱接口")
    @GetMapping(value = "/sendVerifyLoginEmail")
    public Result<FileInfoDto> sendVerifyLoginEmail(@RequestParam(value = "receiver") String receiver) {
        return Result.success("验证注册邮箱！", verifyTemplateService.sendVerifyLoginEmail(receiver));
    }

}
