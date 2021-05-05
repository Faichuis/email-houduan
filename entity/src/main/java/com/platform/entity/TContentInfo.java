package com.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件内容信息表
 *
 * @Author lds
 * @Date 2021/3/7 16:06
 */
@Data
public class TContentInfo implements Serializable {

    /* 主键ID */
    private Long id;

    /* 邮件关联ID */
    private Long emailId;

    /* 邮件内容 */
    private String subject;

    /* 邮件内容 */
    private String content;

    /* 邮件内容类型 */
    private String contentType;

    /* 邮件大小（KB）*/
    private Long contentSize;

    /* 来源邮件类型：1-收件箱，2-发件箱 */
    private String emailType;

    /* 邮箱账号 */
    private String account;

    /* 用户名 */
    private String userCode;

    /* 备注 */
    private String remark;

    /* 数据状态：0-无效，1-有效 */
    private String dataStatus;

    /* 创建人 */
    private String creator;

    /* 创建时间 */
    private Date creatDate;

    /* 修改人 */
    private String modifier;

    /* 修改时间 */
    private Date modifiedDate;

    /* 版本号 */
    private Long versions;

}


