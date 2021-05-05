package com.platform.serve.inner.templatemail.service;

/**
 * 验证邮件信息简单模板
 *
 * @Author lds
 * @Date 2021/4/15 10:44
 */
public interface VerifyTemplateService {

    /**
     * 验证注册邮箱
     *
     * @param receiver
     * @return
     */
    String sendVerifyLoginEmail(String receiver);

}
