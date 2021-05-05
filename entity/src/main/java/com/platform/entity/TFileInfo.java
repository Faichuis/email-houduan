package com.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 附件信息表
 *
 * @Author lds
 * @Date 2021/3/7 16:06
 */
@Data
public class TFileInfo implements Serializable {

    /* 主键ID */
    private Long id;

    /* 邮件关联ID */
    private Long emailId;

    /* 原始文件名 */
    private String originalFileName;

    /* 存储地址 */
    private String filePath;

    /* 存储文件名*/
    private String pathName;

    /* 邮件大小（KB）*/
    private Long fileSize;

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


