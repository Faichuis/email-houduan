package com.platform.serve.inner.share.service;

import com.platform.serve.inner.dto.EmailInfoDto;
import com.platform.serve.inner.dto.FileInfoDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件存储和读取处理类
 *
 * @author lids
 * @version 1.0
 * @date 2021/2/28 20:59
 */
public interface FileService {

    /**
     * 将文件字节数组写入本地指定位置
     *
     * @param fileDownList
     */
    void saveDiskFile(List<FileInfoDto> fileDownList);

    /**
     * 读取本地文件--->字节数组
     *
     * @param filePath
     * @return
     */
    byte[] readDiskFile(String filePath);

    /**
     * 单个附件上传
     *
     * @param file
     * @param userCode
     * @return
     */
    FileInfoDto uploadFile(MultipartFile file, String userCode);

    /**
     * 多个附件上传
     *
     * @param fileList
     * @return
     */
    List<FileInfoDto> uploadFiles(List<MultipartFile> fileList, String userCode);

    /**
     * 单个附件下载
     *
     * @param id
     */
    void downloadFile(Long id);
}
