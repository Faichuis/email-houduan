package com.platform.serve.inner.templatemail.service.impl;

import com.platform.serve.common.constant.Constants;
import com.platform.serve.common.util.CommonUtils;
import com.platform.serve.common.util.EMailSimpleUtils;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.inner.dto.EmailInfoDto;
import com.platform.serve.inner.dto.SendEmailResultDto;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import com.platform.serve.inner.templatemail.service.VerifyTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 验证邮件信息简单模板
 *
 * @Author lds
 * @Date 2021/4/15 10:43
 */
@Slf4j
@Service
public class VerifyTemplateServiceImpl implements VerifyTemplateService {
    //邮箱的SMTP服务器地址，不同邮件服务器地址不同，一般格式为：smtp.xxx.com
    @Value("${simple.mail.server}")
    private String mailServer;

    //发件人
    @Value("${simple.mail.sender}")
    private String sender;

    //端口
    @Value("${simple.mail.smtpPort}")
    private String smtpPort;

    //smtp服务器的认证资料
    @Value("${simple.mail.username}")
    private String account;

    //发件人邮箱密码
    @Value("${simple.mail.password}")
    private String password;

    private final static String LONGIN_SUBJECT = "【邮件管理系统】验证绑定邮箱";


    @Override
    public String sendVerifyLoginEmail(String receiver) {
        UserMailBoxInfoDto userMailBoxInfoDto = new UserMailBoxInfoDto();
        userMailBoxInfoDto.setHostCode(mailServer);
        userMailBoxInfoDto.setPortCode(smtpPort);
        userMailBoxInfoDto.setAccount(account);
        userMailBoxInfoDto.setPassword(password);
        EmailInfoDto emailInfoDto = new EmailInfoDto();
        emailInfoDto.setFromAddress(sender);
        emailInfoDto.setReceivers(receiver);
        emailInfoDto.setSubject(LONGIN_SUBJECT);
        //随机验证码
        String code = CommonUtils.random6Code();
        String content = new StringBuilder("【邮件管理系统】绑定邮箱验证，您的验证码为【").append(code).append("】有效时间为").append(Constants.VERIFY_LONGIN_MIN).append("分钟，请不要把验证码泄露给其他人。如非本人操作，请勿理会！").toString();
        emailInfoDto.setContent(content);
        SendEmailResultDto sendEmailResultDto = EMailSimpleUtils.sendSimpleMail(emailInfoDto, userMailBoxInfoDto);
        if (Constants.SEND_STATUE_NO.equals(sendEmailResultDto.getSendStatus())) {
            throw new CheckException("发送验证码失败，请重试！");
        }
        return code;
    }


}
