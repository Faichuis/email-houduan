package com.platform.serve.inner.service.impl;

import com.platform.serve.common.constant.Constants;
import com.platform.serve.common.util.CommonUtils;
import com.platform.serve.common.util.SnowFlakeUtil;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.inner.dto.EmailInfoDto;
import com.platform.serve.inner.dto.FileInfoDto;
import com.platform.serve.inner.dto.SendEmailResultDto;
import com.platform.serve.inner.service.EmailManageService;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import com.platform.serve.inner.service.SendEmailService;
import com.platform.serve.mapper.TUserMailboxInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;

/**
 * 发送邮件Service
 *
 * @author lids
 * @version 1.0
 * @date 2020/12/27 16:36
 */
@Slf4j
@Service
public class SendEmailServiceImpl implements SendEmailService {

    @Resource
    private TUserMailboxInfoMapper tUserMailboxInfoMapper;
    @Resource
    private EmailManageService emailManageService;


    @Transactional
    @Override
    public void sendEmailMessage(EmailInfoDto emailInfoDto) {
        //校验数据
        checkSendEmailInfoDto(emailInfoDto);

        //查询协议信息
        List<UserMailBoxInfoDto> mailBoxInfoList = tUserMailboxInfoMapper.getUserMailBoxInfoList(emailInfoDto.getUserCode(), emailInfoDto.getAccount(), Constants.MAIL_TYPE_TRANSPORT, null, null);
        if (CollectionUtils.isEmpty(mailBoxInfoList)) {
            StringBuffer msg = new StringBuffer("未查询到用户：").append(emailInfoDto.getUserCode()).append("，邮箱账号：").append(emailInfoDto.getAccount()).append("的信息！");
            throw new CheckException(msg.toString());
        }
        UserMailBoxInfoDto userMailBoxInfoDto = mailBoxInfoList.get(0);
        //判断用户状态
        if (!Constants.DATA_STATUE_OK.equals(userMailBoxInfoDto.getDataStatus())) {
            StringBuffer msg = new StringBuffer("用户：").append(emailInfoDto.getUserCode()).append("，邮箱账号：").append(emailInfoDto.getAccount()).append("无效或未激活！");
            throw new CheckException(msg.toString());
        }


        SendEmailResultDto sendEmailResultDto;
        if (Constants.SEND_STATUE_OK.equals(emailInfoDto.getSendStatus())) {
            //设置生成Message并发送数据
            sendEmailResultDto = sendMessage(userMailBoxInfoDto, emailInfoDto);
        } else {
            sendEmailResultDto = new SendEmailResultDto();
            sendEmailResultDto.setSendStatus(Constants.SEND_STATUE_NO);
            sendEmailResultDto.setMsg("草拟保存");
        }


        //保存Email
        saveSendEmailMessage(emailInfoDto, sendEmailResultDto);

    }


    /**
     * 校验发送邮件数据
     *
     * @param dto
     */
    private void checkSendEmailInfoDto(EmailInfoDto dto) {
        CommonUtils.checkStringException(dto.getUserCode(), "请输入用户账号！");
        CommonUtils.checkStringException(dto.getAccount(), "请选择发送使用的邮箱！");
        CommonUtils.checkStringException(dto.getFromAddress(), "请选择发送使用的邮箱！");
        CommonUtils.checkStringException(dto.getSubject(), "请输入邮件主题！");
        CommonUtils.checkStringException(dto.getReceivers(), "请输入邮件收件人！");
        CommonUtils.checkStringException(dto.getContent(), "请输入邮件内容！");
        CommonUtils.checkStringException(dto.getOutEmailType(), "请选择发件类型！");
        //处理发送时间
        dto.setMakerDate(CommonUtils.getMakerDate(dto.getMakerDate()));

        //默认优先级：普通
        if (null == dto.getPririty()) {
            dto.setPririty(Constants.PRIORITY_NORMAL);
        }

        //默认数据状态：有效
        if (null == dto.getDataStatus()) {
            dto.setDataStatus(Constants.DATA_STATUE_OK);
        }

        //默认数据状态：发件
        if (null == dto.getSendStatus()) {
            dto.setDataStatus(Constants.SEND_STATUE_OK);
        }

        //默认不回执
        if (null == dto.getIsReplaySign()) {
            dto.setIsReplaySign(Constants.IS_REPLY_SIGN_NO);
        }

    }


    /**
     * 发送邮件主要方法
     *
     * @param mailBoxInfo
     * @param emailInfoDto
     * @return
     */
    private SendEmailResultDto sendMessage(UserMailBoxInfoDto mailBoxInfo, EmailInfoDto emailInfoDto) {
        SendEmailResultDto sendEmailResultDto = new SendEmailResultDto();
        try {

            //准备连接服务器的会话信息参数
            Properties props = System.getProperties();
            //设置验证机制
            props.setProperty("mail.smtp.auth", "true");
            //协议
            props.setProperty(mailBoxInfo.getMailCode(), mailBoxInfo.getMailDesc());
            //端口
            props.setProperty(mailBoxInfo.getPortDesc(), mailBoxInfo.getPortCode());
            //pop3服务器
            props.setProperty(mailBoxInfo.getHostDesc(), mailBoxInfo.getHostCode());

            //Session会话
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailBoxInfo.getAccount(), mailBoxInfo.getPassword());
                }
            });

            //开启debug模式
            session.setDebug(true);

            //创建邮件消息主体
            Message message = new MimeMessage(session);

            //发件人
            message.setFrom(new InternetAddress(mailBoxInfo.getAccount()));

            //主题
            setSubject(message, emailInfoDto);

            //接收人
            message.setRecipients(RecipientType.TO, getRecipients(emailInfoDto.getReceivers()));

            //抄送人
            message.setRecipients(RecipientType.CC, getRecipients(emailInfoDto.getCarbonCopys()));

            //密送人
            message.setRecipients(RecipientType.BCC, getRecipients(emailInfoDto.getBlindCarbonCopys()));

            //是否需要回执
            if (Constants.IS_REPLY_SIGN_YES.equals(emailInfoDto.getIsReplaySign())) {
                message.setHeader("Disposition-Notification-To", Constants.IS_REPLY_SIGN_YES);
            }

            //优先级
            message.setHeader("Priority", setPriority(emailInfoDto.getPririty()));


            //文本附件主体部分
            MimeMultipart allFile = new MimeMultipart();

            //文本内容
            MimeBodyPart contentBodyPart = setContentAndPig(emailInfoDto);
            allFile.addBodyPart(contentBodyPart);

            //附件
            setFileInfo(allFile, emailInfoDto);

            //放到Message消息中
            message.setContent(allFile);

            //保存修改
            message.saveChanges();

            //发送邮件
            Transport.send(message);

            //发送成功标时
            sendEmailResultDto.setSendStatus(Constants.SEND_STATUE_OK);

        } catch (Exception e) {
            log.error("发送邮件失败，{}，错误信息{}", emailInfoDto, e);
            sendEmailResultDto.setSendStatus(Constants.SEND_STATUE_NO);
            //sendEmailResultDto.setMsg(new StringBuilder("发送邮件失败，").append("原因：").append(e.getMessage()).toString());
            sendEmailResultDto.setMsg("发送邮件失败");
        } finally {
            //关闭连接

        }
        return sendEmailResultDto;
    }

    /**
     * 设置优先级：1(High):紧急  3:普通(Normal)  5:低(Low)
     *
     * @param priority
     */
    private String setPriority(Integer priority) {
        if (null != priority && priority == 1) {
            return "High";
        } else if (null != priority && priority == 5) {
            return "Low";
        } else {
            return "Normal";
        }
    }

    /**
     * 设置主题
     *
     * @param message
     * @param emailInfoDto
     * @throws Exception
     */
    private void setSubject(Message message, EmailInfoDto emailInfoDto) throws Exception {
        message.setSubject(emailInfoDto.getSubject());
    }

    /**
     * 收件人，抄送，密送
     *
     * @param address
     * @return
     * @throws Exception
     */
    private InternetAddress[] getRecipients(String address) throws Exception {
        if (StringUtils.isEmpty(address)) {
            return null;
        }
        String reAddress = null;
        if (address.contains("<") && address.contains(">")) {
            StringBuilder addressList = new StringBuilder();
            StringBuilder addrOne = null;
            for (int i = 0; i < address.length(); i++) {
                char c = address.charAt(i);
                if (c == '<') {
                    addrOne = new StringBuilder();
                    continue;
                }
                if (c == '>') {
                    if (addrOne != null && addrOne.toString().contains("@")) {
                        if (addressList.length() == 0) {
                            addressList.append(addrOne);
                        } else {
                            addressList.append(",").append(addrOne);
                        }
                    }
                    addrOne = null;
                    continue;
                }
                if (addrOne != null) {
                    addrOne.append(c);
                }
            }
            reAddress = addressList.toString();
        } else {
            reAddress = address;
        }
        if (StringUtils.isEmpty(reAddress)) {
            return null;
        }
        return InternetAddress.parse(reAddress);
    }

    /**
     * 创建文本和图片
     *
     * @param emailInfoDto
     * @return
     * @throws Exception
     */
    private MimeBodyPart setContentAndPig(EmailInfoDto emailInfoDto) throws Exception {
        MimeMultipart mimeMutiPart = new MimeMultipart("related");
        //MimeMultipart mimeMutiPart = new MimeMultipart();

        /*
        //正文中的图片，暂时没有
        MimeBodyPart picBodyPart = new MimeBodyPart();
        FileDataSource fileDataSource = new FileDataSource(new File(new StringBuilder(fileInfoDto.getFilePath()).append("\\").append(fileInfoDto.getPathName()).toString()));
        picBodyPart.setDataHandler(new DataHandler(fileDataSource));
        picBodyPart.setFileName(fileDataSource.getName());
        mimeMutiPart.addBodyPart(picBodyPart);
        */

        //正文文本
        MimeBodyPart contentBodyPart = new MimeBodyPart();
        contentBodyPart.setContent(emailInfoDto.getContent(), "text/html;charset=utf-8");
        mimeMutiPart.addBodyPart(contentBodyPart);

        //图片和文本结合
        MimeBodyPart allBodyPart = new MimeBodyPart();
        allBodyPart.setContent(mimeMutiPart);
        return allBodyPart;

    }

    /**
     * 创建附件
     *
     * @param emailInfoDto
     * @throws Exception
     */
    private void setFileInfo(MimeMultipart allFile, EmailInfoDto emailInfoDto) throws Exception {
        //附件信息
        List<FileInfoDto> fileInfoDtoList = emailInfoDto.getFileInfoDtoList();
        if (CollectionUtils.isNotEmpty(fileInfoDtoList)) {
            //正文和附件都存在邮件中，所有类型设置为mixed
            allFile.setSubType("mixed");
            for (FileInfoDto fileInfoDto : fileInfoDtoList) {
                //创建附件载体
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                //附件流读取
                FileDataSource dataSource = new FileDataSource(new File(new StringBuilder(fileInfoDto.getFilePath()).append("\\").append(fileInfoDto.getPathName()).toString()));
                mimeBodyPart.setDataHandler(new DataHandler(dataSource));
                //附件名称
                mimeBodyPart.setFileName(fileInfoDto.getOriginalFileName());
                allFile.addBodyPart(mimeBodyPart);
            }
        }
    }


    /**
     * 保存发送数据
     *
     * @param emailInfoDto
     * @param sendEmailResultDto
     */
    private void saveSendEmailMessage(EmailInfoDto emailInfoDto, SendEmailResultDto sendEmailResultDto) {
        emailManageService.saveOutboxInfo(emailInfoDto, sendEmailResultDto.getSendStatus(), sendEmailResultDto.getMsg());
    }


}
