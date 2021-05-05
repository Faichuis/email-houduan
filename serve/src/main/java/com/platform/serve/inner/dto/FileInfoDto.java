package com.platform.serve.inner.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 邮件附件信息
 *
 * @Author lds
 * @Date 2021/3/7 16:06
 */
@Data
public class FileInfoDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "原始文件名")
    private String originalFileName;

    @ApiModelProperty(value = "存储地址")
    private String filePath;

    @ApiModelProperty(value = "存储文件名")
    private String pathName;

    @ApiModelProperty(value = "存储流")
    private byte[] fileBytes;

    @ApiModelProperty(value = "邮件大小（KB）")
    private Long fileSize;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "数据状态：0-无效，1-有效")
    private String dataStatus;

}


