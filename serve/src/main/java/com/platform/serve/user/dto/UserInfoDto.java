package com.platform.serve.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息dto
 *
 * @Author lds
 * @Date 2021/4/12 9:31
 */
@Data
public class UserInfoDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户密码")
    private String userPwd;

    @ApiModelProperty(value = "注册手机号")
    private String loginPhone;

    @ApiModelProperty(value = "注册邮箱号")
    private String loginEmail;

    @ApiModelProperty(value = "验证code")
    private String code;

    @ApiModelProperty(value = "是否有密保问题：0-无，1-有")
    private String securityQuestion;

    @ApiModelProperty(value = "会员等级：0-无，1-会员1级，2-会员2级")
    private String member;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "数据状态：0-无效，1-有效，2-未激活")
    private String dataStatus;

    @ApiModelProperty(value = "版本号")
    private Long versions;

}
