package com.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮箱登录信息表
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/3 21:39
 */
@Data
public class TUserMailboxInfo implements Serializable {

    /** 主键 */
    private Long id;

    /** 用户编码 */
    private String userCode;

    /** 用户名 */
    private String userName;

    /** 发邮箱账号 */
    private String account;

    /** 邮箱密码 */
    private String password;

    /** 接收协议：pop3、imap */
    private String storeCode;

    /** 发送协议：pop3、imap */
    private String transportCode;

    /** 邮箱后缀 */
    private String mailboxSuffix;

    /** 备注 */
    private String remark;

    /** 数据状态：0-无效，1-有效，2-失效 */
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
