package com.platform.serve.common.util;

import com.platform.serve.common.constant.Constants;
import com.platform.serve.inner.dto.EmailInfoDto;
import com.platform.serve.inner.dto.SendEmailResultDto;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * 简单的邮件工具类
 *
 * @Author lds
 * @Date 2021/4/15 10:47
 */
@Slf4j
public class EMailSimpleUtils {
    private final static String EMAIL_PET_NAME = "【邮件管理系统】";

    public static SendEmailResultDto sendSimpleMail(EmailInfoDto emailInfoDto, UserMailBoxInfoDto userMailBoxInfoDto) {
        log.info("发送简单邮件开始：");
        SendEmailResultDto sendEmailResultDto = new SendEmailResultDto();
        try {
            //发送邮件
            HtmlEmail email = new HtmlEmail();
            //通过Gmail Server发送邮件
            email.setHostName(userMailBoxInfoDto.getHostCode());
            email.setSmtpPort(Integer.parseInt(userMailBoxInfoDto.getPortCode()));
            //设定smtp服务器的认证资料信息
            email.setAuthentication(userMailBoxInfoDto.getAccount(), userMailBoxInfoDto.getPassword());
            email.setStartTLSEnabled(false);
            email.setSSLOnConnect(false);

            //设定发件人
            email.setFrom(emailInfoDto.getFromAddress(), EMAIL_PET_NAME);
            //设定收件人
            email.addTo(emailInfoDto.getReceivers());
            //设定内容的语言集
            email.setCharset("UTF-8");
            //开启Debug模式
            email.setDebug(true);
            //设定主题
            email.setSubject(emailInfoDto.getSubject());
            //设定邮件内容
            email.setHtmlMsg(emailInfoDto.getContent());
            //发送邮件
            email.send();

            //发送成功标时
            sendEmailResultDto.setSendStatus(Constants.SEND_STATUE_OK);

        } catch (EmailException e) {
            log.error("发送简单邮件错误，内容：{}，错误{}，异常：", emailInfoDto, e.getMessage(), e);
            sendEmailResultDto.setSendStatus(Constants.SEND_STATUE_NO);
            sendEmailResultDto.setMsg(new StringBuilder("发送简单邮件失败，").append("原因：").append(e.getMessage()).toString());
        }
        log.info("发送简单邮件结束！");
        return sendEmailResultDto;
    }
}
