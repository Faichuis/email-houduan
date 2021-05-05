package com.platform.serve.inner.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 发送接收邮件参数实体类
 *
 * @author lids
 * @version 1.0
 * @date 2020/12/27 17:48
 */

@Data
public class EmailPropertiesDto implements Serializable {

    private String protocolName;

    private String protocol;

    private String portName;

    private String port;

    private String hostName;

    private String host;

    private String store;

    private String mailUser;

    private String mailPassWord;

}
