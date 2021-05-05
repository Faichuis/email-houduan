package com.platform.serve.inner.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 邮件主题类
 *
 * @author lids
 * @version 1.0
 * @date 2020/12/20 21:33
 */
@Data
public class EmailInfoDto implements Serializable {

    @ApiModelProperty(value = "服务器邮件ID")
    private String messageId;

    @ApiModelProperty(value = "邮件关联ID")
    private Long emailId;

    @ApiModelProperty(value = "用户名编码")
    private String userCode;

    @ApiModelProperty(value = "邮箱号")
    private String account;

    @ApiModelProperty(value = "邮件主题")
    private String subject;

    @ApiModelProperty(value = "邮件发件人")
    private String fromAddress;

    @ApiModelProperty(value = "邮件收件人")
    private String receivers;

    @ApiModelProperty(value = "邮件抄送人")
    private String carbonCopys;

    @ApiModelProperty(value = "邮件密送人")
    private String blindCarbonCopys;

    @ApiModelProperty(value = "用户已读")
    private String userSeen;

    @ApiModelProperty(value = "是否已读")
    private String isSeen;

    @ApiModelProperty(value = "优先级")
    private Integer pririty;

    @ApiModelProperty(value = "是否需要回执")
    private String isReplaySign;

    @ApiModelProperty(value = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date makerDate;

    @ApiModelProperty(value = "邮件大小")
    private Long size;

    @ApiModelProperty(value = "邮件内容")
    private String content;

    @ApiModelProperty(value = "发件类型：0-回复，1-发件，2-转发")
    private String outEmailType;

    @ApiModelProperty(value = "发送状态：0-草稿，1-成功")
    private String sendStatus;

    @ApiModelProperty(value = "数据状态：0-无效，1-有效，2-草稿")
    private String dataStatus;

    @ApiModelProperty(value = "附件信息集合")
    private List<FileInfoDto> fileInfoDtoList;


    @ApiModelProperty(value = "当前页页码")
    private Long page;

    @ApiModelProperty(value = "每页条数")
    private Long pageSize;

}