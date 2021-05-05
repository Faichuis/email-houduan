package com.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮箱协议实体类
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/3 17:37
 */

@Data
public class BscSmtpProtocolT implements Serializable {

    /** 主键 */
    private Long id;

    /** 接收or发送协议：pop3、imap */
    private String mailCode;

    /** 接收or发送协议key */
    private String mailDesc;

    /** 发类型：1、接收；2、发送 */
    private String mailType;

    /** 服务器地址值 */
    private String hostCode;

    /** 服务器地址key */
    private String hostDesc;

    /** 端口 */
    private String portCode;

    /** 端口key */
    private String portDesc;

    /** 邮箱后缀 */
    private String mailboxSuffix;

    /** 备注 */
    private String remark;

    /** 数据状态：0-无效，1-有效 */
    private String dataStatus;

    /** 创建人 */
    private String creator;

    /* 创建时间 */
    private Date creatDate;

    /* 修改人 */
    private String modifier;

    /* 修改时间 */
    private Date modifiedDate;

    /** 版本 */
    private String versions;

}
