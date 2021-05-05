package com.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 *
 * @Author lds
 * @Date 2021/4/9 14:48
 */
@Data
public class BscUserT implements Serializable {

    /* 主键ID */
    private Long id;

    /* 用户编码 */
    private String userCode;

    /* 用户名 */
    private String userName;

    /* 用户密码 */
    private String userPwd;

    /* 注册手机号 */
    private String loginPhone;

    /* 注册邮箱号 */
    private String loginEmail;

    /* 是否有密保问题：0-无，1-有 */
    private String securityQuestion;

    /* 会员等级：0-无，1-会员1级，2-会员2级 */
    private String member;

    /* 备注 */
    private String remark;

    /* 激活状态：0-未激活，1-激活 */
    private String activateStatus;

    /* 激活时间 */
    private Date activateDate;

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
