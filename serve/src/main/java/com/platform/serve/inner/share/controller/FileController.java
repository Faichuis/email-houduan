package com.platform.serve.inner.share.controller;

import com.platform.serve.common.util.RequestUtils;
import com.platform.serve.config.result.Result;
import com.platform.serve.inner.dto.FileInfoDto;
import com.platform.serve.inner.share.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 附件相关
 *
 * @Author lds
 * @Date 2021/4/12 16:59
 */
@Api(value = "file", description = "附件相关业务类")
@RestController
@RequestMapping(path = "/api/file")
public class FileController {

    @Resource
    private FileService fileService;


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "单个附件上传", notes = "单个附件上传接口")
    @PostMapping(value = "/uploadFile")
    public Result<FileInfoDto> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        HttpServletRequest request = RequestUtils.getHttpServletRequest();
        String userCode = request.getHeader("userCode");
        return Result.success("附件上传成功！", fileService.uploadFile(file, userCode));
    }

    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "多个附件上传", notes = "多个附件上传接口")
    @PostMapping(value = "/uploadFiles")
    public Result<List<FileInfoDto>> uploadFiles(HttpServletRequest request) {
        List<MultipartFile> fileList = ((MultipartHttpServletRequest) request).getFiles("file");
        String userCode = (String) ((MultipartHttpServletRequest) request).getAttribute("userCode");
        return Result.success("附件上传成功！", fileService.uploadFiles(fileList, userCode));
    }

    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @ApiOperation(value = "单个附件下载", notes = "单个附件下载接口")
    @GetMapping(value = "/downloadFile")
    public Result<?> downloadFile(@RequestParam(value = "id") Long id) {
        fileService.downloadFile(id);
        return Result.success("附件下载成功！");
    }

}
