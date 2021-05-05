package com.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 发件箱主表
 *
 * @Author lds
 * @Date 2021/3/11 14:17
 */
@Data
public class TOutboxMain implements Serializable {

    /* 主键ID */
    private Long id;

    /* 邮件关联ID */
    private Long emailId;

    /* 主题 */
    private String subject;

    /* 发件人 */
    private String fromAddress;

    /* 收件人 */
    private String receivers;

    /* 抄送人*/
    private String carbonCopys;

    /* 密送人*/
    private String blindCarbonCopys;

    /* 发件类型：0-回复，1-发件，2-转发 */
    private String outEmailType;

    /* 优先级：1-紧急，3-普通，5-低 */
    private Integer pririty;

    /* 是否需要回执：0-不回执，1-需要回执 */
    private String isReplaySign;

    /* 是否存在附件：0-无，1-有 */
    private String containFile;

    /* 邮件发送时间 */
    private Date makerDate;

    /* 发送状态：0-草稿，1-成功 */
    private String sendStatus;

    /* 邮件大小（KB）*/
    private Long size;

    /* 邮箱账号 */
    private String account;

    /* 使用昵称 */
    private String petName;

    /* 用户名 */
    private String userCode;

    /* 备注 */
    private String remark;

    /* 数据状态：0-无效，1-有效，2-草稿 */
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
