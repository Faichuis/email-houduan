package com.platform.serve.inner.share.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 个人邮箱及其链接信息
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/3 22:16
 */
@Data
public class UserMailBoxInfoDto implements Serializable {

    @ApiModelProperty(value = "用户关联邮箱主信息ID")
    private Long userMailBoxId;

    @ApiModelProperty(value = "用户名编码")
    private String userCode;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "邮箱账号")
    private String account;

    @ApiModelProperty(value = "邮箱密码")
    private String password;

    @ApiModelProperty(value = "接收or发送协议：pop3、imap")
    private String mailCode;

    @ApiModelProperty(value = "接收or发送协议key")
    private String mailDesc;

    @ApiModelProperty(value = "类型：1、接收；2、发送")
    private String mailType;

    @ApiModelProperty(value = "服务器地址值")
    private String hostCode;

    @ApiModelProperty(value = "服务器地址key")
    private String hostDesc;

    @ApiModelProperty(value = "端口")
    private String portCode;

    @ApiModelProperty(value = "端口key")
    private String portDesc;

    @ApiModelProperty(value = "邮箱后缀")
    private String mailboxSuffix;

    @ApiModelProperty(value = "数据状态：0-无效，1-有效，2-失效")
    private String dataStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

}
