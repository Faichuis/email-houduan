package com.platform.serve.inner.share.service.impl;

import com.platform.entity.TFileInfo;
import com.platform.serve.common.constant.Constants;
import com.platform.serve.common.util.CommonUtils;
import com.platform.serve.common.util.RequestUtils;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.inner.dto.FileInfoDto;
import com.platform.serve.inner.share.service.FileService;
import com.platform.serve.mapper.TFileInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件存储和读取处理类(本地交互使用)
 *
 * @author lids
 * @version 1.0
 * @date 2021/2/28 21:00
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private TFileInfoMapper tFileInfoMapper;

    @Override
    public void downloadFile(Long id) {
        //校验入参
        CommonUtils.checkLongException(id, "请传入下载附件的ID");
        //查询附件信息
        TFileInfo tFileInfo = tFileInfoMapper.selectById(id);

        if (tFileInfo == null) {
            throw new CheckException("未查询到附件信息！");
        }
        //读取流
        byte[] bytes = readDiskFile(tFileInfo.getFilePath() + File.separator + tFileInfo.getPathName());
        if (bytes == null) {
            throw new CheckException("未读取到《" + tFileInfo.getOriginalFileName() + "》附件");
        }

        OutputStream outputStream = null;
        String fileName;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            //设置名称
            if (CommonUtils.isMSBrowser(request)) {
                fileName = URLEncoder.encode(tFileInfo.getOriginalFileName(), "utf-8");
            } else {
                fileName = new String(tFileInfo.getOriginalFileName().getBytes("utf-8"), "ISO-8859-1");
            }
            //清除buffer缓存
            response.reset();
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);

            //下载附件
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();

        } catch (Exception e) {
            throw new CheckException("服务器开小差了，下载失败！");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.info("关流失败！");
                }
            }
        }

    }


    @Override
    public void saveDiskFile(List<FileInfoDto> fileDownList) {
        if (CollectionUtils.isNotEmpty(fileDownList)) {
            for (FileInfoDto fileInfoDto : fileDownList) {
                //异步保存到本地
                asyncStorageFileByte(fileInfoDto.getFileBytes(), fileInfoDto.getFilePath(), fileInfoDto.getPathName());
            }

        }
    }

    @Override
    public byte[] readDiskFile(String filePath) {
        if (StringUtils.isNotEmpty(filePath)) {
            return readFileByte(filePath);
        } else {
            log.info("未获取到指定地址的文件，传入的文件地址未空。");
            return null;
        }

    }


    @Override
    public FileInfoDto uploadFile(MultipartFile multipartFile, String userCode) {
        FileInfoDto fileInfoDto = new FileInfoDto();
        try {
            //附件信息
            fileInfoDto.setOriginalFileName(multipartFile.getOriginalFilename());
            fileInfoDto.setFilePath(CommonUtils.getFileAbsolutePath(userCode));
            fileInfoDto.setPathName(CommonUtils.getUUID32());
            fileInfoDto.setDataStatus(Constants.DATA_STATUE_OK);
            fileInfoDto.setFileSize((long) multipartFile.getBytes().length);
            //写入本地
            storageFileByte(multipartFile.getBytes(), fileInfoDto.getFilePath(), fileInfoDto.getPathName());
        } catch (Exception e) {
            log.error("附件上传失败", e);
            throw new CheckException("附件上传失败，请重试！");
        }
        return fileInfoDto;
    }


    @Override
    public List<FileInfoDto> uploadFiles(List<MultipartFile> fileList, String userCode) {
        List<FileInfoDto> fileInfoDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fileList)) {
            for (MultipartFile multipartFile : fileList) {
                FileInfoDto fileInfoDto = uploadFile(multipartFile, userCode);
                fileInfoDtoList.add(fileInfoDto);
            }
        }
        return fileInfoDtoList;
    }


    @Async(value = "asyncTaskExecutor")
    public void asyncStorageFileByte(byte[] buf, String filePath, String fileName) {
        //保存到本地
        storageFileByte(buf, filePath, fileName);
    }


    /**
     * 将文件字节数组写入本地指定位置
     *
     * @param buf
     * @param filePath
     * @param fileName
     */
    private void storageFileByte(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        String fileAllPath = null;
        try {
            //文件全地址
            fileAllPath = filePath + File.separator + fileName;
            //文件目录
            File dir = new File(filePath);
            //不存在则创建文件目录
            if (!dir.exists() && !dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(fileAllPath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
            bos.flush();
        } catch (Exception e) {
            log.error("存储文件失败，文件地址", fileAllPath, e);
        } finally {
            //关闭流
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 读取本地文件--->字节数组
     *
     * @param filePath 文件地址：含文件名及其后缀
     * @return
     */
    private byte[] readFileByte(String filePath) {
        byte[] buffer = null;
        InputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            log.error("未获取到指定地址{}的文件！", filePath, e);
        } catch (IOException e) {
            log.error("获取到指定地址{}的文件失败！", filePath, e);
        } finally {
            //关闭流
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }


}
