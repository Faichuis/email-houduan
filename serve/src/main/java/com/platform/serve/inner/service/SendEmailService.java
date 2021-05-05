package com.platform.serve.inner.service;

import com.platform.serve.inner.dto.EmailInfoDto;

/**
 * 发送邮件Service
 *
 * @author lids
 * @version 1.0
 * @date 2020/12/27 16:38
 */
public interface SendEmailService {

    /**
     * 发送邮件
     *
     * @param emailInfoDto
     */
    void sendEmailMessage(EmailInfoDto emailInfoDto);


}
