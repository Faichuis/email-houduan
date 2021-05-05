package com.platform.serve.inner.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送邮件返回结果
 *
 * @Author lds
 * @Date 2021/4/15 10:28
 */
@Data
public class SendEmailResultDto implements Serializable {

    /* 发送状态：0-失败，1-成功 */
    private String sendStatus;

    /* 信息 */
    private String msg;

    /* 异常 */
    private RuntimeException e;

}
