package com.platform.serve.inner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.entity.BscSmtpProtocolT;
import com.platform.entity.TUserMailboxInfo;
import com.platform.serve.common.constant.Constants;
import com.platform.serve.common.util.CommonUtils;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.inner.dto.EmailInfoDto;
import com.platform.serve.inner.dto.FileInfoDto;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import com.platform.serve.inner.service.EmailManageService;
import com.platform.serve.inner.service.ReadEmailService;
import com.platform.serve.inner.share.service.SmtpProtocolService;
import com.platform.serve.mapper.TInboxMainMapper;
import com.platform.serve.mapper.TUserMailboxInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 读取邮件service
 *
 * @author lids
 * @version 1.0
 * @date 2020/12/27 16:40
 */
@Slf4j
@Service
public class ReadEmailServiceImpl implements ReadEmailService {

    @Resource
    private SmtpProtocolService smtpProtocolService;
    @Resource
    private TUserMailboxInfoMapper tUserMailboxInfoMapper;
    @Resource
    private EmailManageService emailManageService;
    @Resource
    private TInboxMainMapper tInboxMainMapper;

    /**
     * 判断账号是否可用
     *
     * @param dto
     */
    @Override
    public Boolean judgeMailUserIsNormal(UserMailBoxInfoDto dto) {
        //校验数据
        checkJudgeMailUserIsNormal(dto);

        //账号是否可用：默认否
        boolean judgeMailUserIsNormal = false;
        Store store = null;
        try {
            //查询连接信息
            BscSmtpProtocolT bscSmtpProtocolT = new BscSmtpProtocolT();
            bscSmtpProtocolT.setMailboxSuffix(dto.getMailboxSuffix());
            bscSmtpProtocolT.setMailType(Constants.MAIL_TYPE_STORE);
            bscSmtpProtocolT.setMailCode(Constants.MAIL_CODE_STORE);
            BscSmtpProtocolT reBscSmtpProtocolT = smtpProtocolService.getSmtpProtocolByMailboxSuffix(bscSmtpProtocolT);

            //准备连接服务器的会话信息参数
            Properties props = System.getProperties();
            //协议
            props.setProperty(reBscSmtpProtocolT.getMailCode(), reBscSmtpProtocolT.getMailDesc());
            //端口
            props.setProperty(reBscSmtpProtocolT.getPortDesc(), reBscSmtpProtocolT.getPortCode());
            //pop3服务器
            props.setProperty(reBscSmtpProtocolT.getHostDesc(), reBscSmtpProtocolT.getHostCode());

            //创建Session实例对象
            Session session = Session.getInstance(props);
            session.setDebug(false);
            store = session.getStore(reBscSmtpProtocolT.getMailCode());
            store.connect(dto.getAccount(), dto.getPassword());
            //账号可用
            judgeMailUserIsNormal = true;
        } catch (Exception e) {
            String msg = new StringBuilder("该账号或密码不正确，或未开启").append(Constants.MAIL_CODE_STORE).append("/").append(Constants.MAIL_CODE_TRANSPORT).append("授权，请重试！").toString();
            log.error(msg);
            //throw new CheckException(msg);
        } finally {
            //关闭连接
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        return judgeMailUserIsNormal;
    }

    /**
     * 判断账号是否可用校验
     *
     * @param dto
     */
    private void checkJudgeMailUserIsNormal(UserMailBoxInfoDto dto) {

        if (StringUtils.isEmpty(dto.getPassword())) {
            throw new CheckException("请输入邮箱密码！");
        }
        if (StringUtils.isEmpty(dto.getAccount())) {
            throw new CheckException("请输入邮箱账号！");
        }
        if (StringUtils.isEmpty(dto.getMailboxSuffix())) {
            throw new CheckException("请输入邮箱后缀数！");
        }

    }


    /**
     * 读取邮件
     *
     * @param dto
     */
    @Override
    public void readEmail(UserMailBoxInfoDto dto) {
        //读取邮件校验
        checkReadEmail(dto);

        //查询协议信息
        List<UserMailBoxInfoDto> mailBoxInfoList = tUserMailboxInfoMapper.getUserMailBoxInfoList(dto.getUserCode(), dto.getAccount(), Constants.MAIL_TYPE_STORE, dto.getMailboxSuffix(), Constants.DATA_STATUE_OK);
        if (CollectionUtils.isEmpty(mailBoxInfoList)) {
            throw new CheckException("请输入邮箱后缀等参数！");
        }
        UserMailBoxInfoDto mailBoxInfo = mailBoxInfoList.get(0);
        List<EmailInfoDto> emailInfoList = new ArrayList<>();
        Store store = null;
        try {
            //准备连接服务器的会话信息参数
            Properties props = System.getProperties();
            //协议
            props.setProperty(mailBoxInfo.getMailCode(), mailBoxInfo.getMailDesc());
            //端口
            props.setProperty(mailBoxInfo.getPortDesc(), mailBoxInfo.getPortCode());
            //pop3服务器
            props.setProperty(mailBoxInfo.getHostDesc(), mailBoxInfo.getHostCode());

            //创建Session实例对象
            Session session = Session.getInstance(props);
            session.setDebug(false);
            store = session.getStore(mailBoxInfo.getMailCode());
            store.connect(mailBoxInfo.getAccount(), mailBoxInfo.getPassword());
            Folder folder = store.getFolder("INBOX");

            //打开收件箱：Folder.READ_ONLY：只读权限；Folder.READ_WRITE：可读可写（可以修改邮件的状态）
            folder.open(Folder.READ_WRITE);

            //由于POP3协议无法获知邮件的状态,所以getUnreadMessageCount得到的是收件箱的邮件总数
            System.out.println("未读邮件数: " + folder.getUnreadMessageCount());

            Message messages[] = folder.getMessages();
            if (messages != null && messages.length > 0) {
                //查询邮件的key，防止重复读取。

                //根据用户编码和邮箱账号查询服务器ID
                List<String> messageIdList = tInboxMainMapper.getMessageIdListByAccount(mailBoxInfo.getUserCode(), mailBoxInfo.getAccount(), null);

                for (int i = 0; i < messages.length; i++) {
                    MimeMessage message = (MimeMessage) messages[i];
                    //比较key是否存在，存在则跳过。
                    if (CollectionUtils.isNotEmpty(messageIdList) && messageIdList.contains(message.getMessageID())) {
                        continue;
                    }

                    try {
                        //解析邮件
                        EmailInfoDto emailInfo = analysisReadEmail(message, mailBoxInfo);
                        if (emailInfo != null) {
                            emailInfoList.add(emailInfo);
                        }
                    } catch (Exception e) {
                        log.error("解析邮件失败。服务器ID：{}，用户编码：{}，邮箱账号：{}，失败信息{}", message.getMessageID(), mailBoxInfo.getUserCode(), mailBoxInfo.getAccount(), e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("接收邮件失败。用户编码：{}，邮箱账号：{}，失败信息{}", mailBoxInfo.getUserCode(), mailBoxInfo.getAccount(), e);
        } finally {
            //关闭连接
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        //保存邮件信息
        emailManageService.saveInboxInfo(emailInfoList);

    }

    @Override
    public void readEmailByUserCode(UserMailBoxInfoDto dto) {
        //校验数据
        CommonUtils.checkStringException(dto.getUserCode(), "请输入用户名！");

        //查询有效的关联数据
        QueryWrapper<TUserMailboxInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", dto.getUserCode());
        queryWrapper.eq("data_status", Constants.DATA_STATUE_OK);
        List<TUserMailboxInfo> mailboxInfos = tUserMailboxInfoMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(mailboxInfos)) {
            for (TUserMailboxInfo mailboxInfo : mailboxInfos) {
                try {
                    UserMailBoxInfoDto userMailBoxInfoDto = new UserMailBoxInfoDto();
                    userMailBoxInfoDto.setUserCode(mailboxInfo.getUserCode());
                    userMailBoxInfoDto.setMailboxSuffix(mailboxInfo.getMailboxSuffix());
                    userMailBoxInfoDto.setAccount(mailboxInfo.getAccount());
                    readEmail(userMailBoxInfoDto);
                } catch (Exception e) {
                    log.error("接收用户{}的Email失败{}", mailboxInfo.getUserCode(), mailboxInfo.getAccount());
                }
            }
        }

    }

    /**
     * 读取邮件校验
     *
     * @param dto
     */
    private void checkReadEmail(UserMailBoxInfoDto dto) {
        CommonUtils.checkStringException(dto.getUserCode(), "请输入用户名！");
        CommonUtils.checkStringException(dto.getAccount(), "请输入邮箱账号！");
        CommonUtils.checkStringException(dto.getMailboxSuffix(), "请输入邮箱后缀！");

    }


    /**
     * 解析收件信息
     *
     * @param message
     */
    private EmailInfoDto analysisReadEmail(MimeMessage message, UserMailBoxInfoDto userMailBoxInfo) throws Exception {
        EmailInfoDto emailInfoDto = null;
        if (message != null) {
            log.info("解析邮件开始。服务器ID：{}，用户编码：{}，邮箱账号：{}", message.getMessageID(), userMailBoxInfo.getUserCode(), userMailBoxInfo.getAccount());
            emailInfoDto = new EmailInfoDto();
            emailInfoDto.setUserCode(userMailBoxInfo.getUserCode());
            emailInfoDto.setAccount(userMailBoxInfo.getAccount());
            //emailInfoDto.setEmailId(SnowFlakeUtil.getSnowFlakeId());
            emailInfoDto.setMessageId(message.getMessageID());
            emailInfoDto.setSubject(getSubject(message));
            emailInfoDto.setFromAddress(getFrom(message));
            emailInfoDto.setReceivers(getReceiveAddress(message, Message.RecipientType.TO));
            emailInfoDto.setCarbonCopys(getReceiveAddress(message, Message.RecipientType.CC));
            emailInfoDto.setBlindCarbonCopys(getReceiveAddress(message, Message.RecipientType.BCC));
            emailInfoDto.setMakerDate(getSentDate(message));
            emailInfoDto.setIsSeen(isSeen(message) ? Constants.IS_SEEN_NO : Constants.IS_SEEN_YES);
            emailInfoDto.setPririty(getPriority(message));
            emailInfoDto.setIsReplaySign(isReplySign(message) ? Constants.IS_REPLY_SIGN_YES : Constants.IS_REPLY_SIGN_NO);
            //int size = message.getSize();
            //System.out.println("邮件大小：" + message.getSize() * 1024 + "kb");
            boolean isContainerAttachment = isContainAttachment(message);

            //存在附件，解析附件信息
            if (isContainerAttachment) {
                List<FileInfoDto> fileInfoList = new ArrayList<>();
                emailInfoDto.setFileInfoDtoList(fileInfoList);
                //保存附件
                saveAttachment(message, emailInfoDto, fileInfoList);
            }
            //邮件正文解析
            StringBuffer content = new StringBuffer(30);
            getMailTextContent(message, content);
            emailInfoDto.setContent(content.toString());
            log.info("解析邮件结束。服务器ID：{}，用户编码：{}，邮箱账号：{}", message.getMessageID() + userMailBoxInfo.getUserCode(), userMailBoxInfo.getAccount());

        }
        return emailInfoDto;
    }


    /**
     * 获得邮件主题
     *
     * @param msg 邮件信息
     * @return 解码后的邮件主题
     * @throws Exception
     */
    private String getSubject(MimeMessage msg) throws Exception {
        return MimeUtility.decodeText(msg.getSubject());
    }


    /**
     * 获得邮件发件人 - 必有
     *
     * @param msg 邮件信息
     * @return 姓名 <Email地址>
     * @throws Exception
     */
    private String getFrom(MimeMessage msg) throws Exception {
        String from;
        Address[] froms = msg.getFrom();
        //没有发件人抛出异常
        if (froms.length < 1) {
            throw new CheckException("没有发件人!");
        }

        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + Constants.ADDRESS_HEAD + address.getAddress() + Constants.ADDRESS_TRAIL;

        return from;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     *
     * @param msg  邮件信息
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws Exception
     */
    private String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws Exception {
        //收件人（抄送、密送）信息
        StringBuffer addressStr = new StringBuffer();
        //地址集
        Address[] addresss = null;
        //类型空，读取全部地址集
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            //读取指定类型地址集
            addresss = msg.getRecipients(type);
        }

        //如果不存在返回null
        if (addresss == null || addresss.length < 1)
            return null;
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress) address;
            addressStr.append(internetAddress.toUnicodeString()).append(",");
        }

        //删除最后一个逗号
        addressStr.deleteCharAt(addressStr.length() - 1);

        return addressStr.toString();
    }


    /**
     * 获得邮件发送时间
     *
     * @param msg 邮件信息
     * @return Date
     * @throws Exception
     */
    private Date getSentDate(MimeMessage msg) throws Exception {
        return msg.getSentDate();
    }


    /**
     * 判断邮件中是否包含附件
     *
     * @param part 邮件信息
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws Exception
     */
    private boolean isContainAttachment(Part part) throws Exception {
        //默认没有附件
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                    return flag;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                        return flag;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                        return flag;
                    }
                }

            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }


    /**
     * 判断邮件是否已读  www.2cto.com
     *
     * @param msg 邮件信息
     * @return 如果邮件已读返回true, 否则返回false
     * @throws Exception
     */
    private boolean isSeen(MimeMessage msg) throws Exception {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }


    /**
     * 判断邮件是否需要阅读回执
     *
     * @param msg 邮件信息
     * @return 需要回执返回true, 否则返回false
     * @throws Exception
     */
    private boolean isReplySign(MimeMessage msg) throws Exception {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null)
            replySign = true;
        return replySign;
    }


    /**
     * 获得邮件的优先级
     *
     * @param msg 邮件信息
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws Exception
     */
    private Integer getPriority(MimeMessage msg) throws Exception {
        int priority = 3;
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = Constants.PRIORITY_HIGH;
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = Constants.PRIORITY_LOW;
            else
                priority = Constants.PRIORITY_NORMAL;
        }
        return priority;
    }


    /**
     * 保存附件
     *
     * @param part：件中多个组合体中的其中一个组合体
     * @param fileInfoList：附件信息集合
     * @param emailInfo：邮件信息集合
     * @throws Exception
     */
    private void saveAttachment(Part part, EmailInfoDto emailInfo, List<FileInfoDto> fileInfoList) throws Exception {
        if (part.isMimeType("multipart/*")) {
            //复杂体邮件
            Multipart multipart = (Multipart) part.getContent();
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    FileInfoDto fileInfoDto = getFileInfo(is, emailInfo, decodeText(bodyPart.getFileName()));
                    if (fileInfoDto != null) {
                        fileInfoList.add(fileInfoDto);
                    }
                } else if (bodyPart.isMimeType("multipart/*")) {
                    //递归
                    saveAttachment(bodyPart, emailInfo, fileInfoList);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        FileInfoDto fileInfoDto = getFileInfo(bodyPart.getInputStream(), emailInfo, decodeText(bodyPart.getFileName()));
                        if (fileInfoDto != null) {
                            fileInfoList.add(fileInfoDto);
                        }
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            //递归
            saveAttachment((Part) part.getContent(), emailInfo, fileInfoList);
        }
    }


    /**
     * 读取附件输入流中等数据存储到对象中返回
     *
     * @param is：输入流
     * @param fileName：文件名
     * @param emailInfo：邮件信息
     * @throws Exception
     */
    private FileInfoDto getFileInfo(InputStream is, EmailInfoDto emailInfo, String fileName) {
        FileInfoDto fileInfoDto = null;
        ByteArrayOutputStream output = null;
        try {
            if (is != null) {
                //生成对象
                fileInfoDto = new FileInfoDto();
                fileInfoDto.setFilePath(CommonUtils.getFileAbsolutePath(emailInfo.getUserCode()));
                fileInfoDto.setOriginalFileName(fileName);
                fileInfoDto.setPathName(CommonUtils.getUUID32());
                //处理文件流
                output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 4];
                int n = 0;
                while (-1 != (n = is.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                //output.flush();
                byte[] bytes = output.toByteArray();
                fileInfoDto.setFileBytes(bytes);
                fileInfoDto.setFileSize((long) bytes.length);
            }
        } catch (Exception e) {
            log.error("解析附件错误，错误信息：{}", e);
            throw new CheckException("解析附件错误！");
        } finally {
            //关闭流
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileInfoDto;
    }


    /**
     * 文本解码
     *
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws Exception
     */
    private String decodeText(String encodeText) throws Exception {
        if (StringUtils.isEmpty(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }


    /**
     * 获得邮件文本内容
     *
     * @param part：邮件体
     * @param content：存储邮件文本内容的字符串
     * @throws Exception
     */
    private void getMailTextContent(Part part, StringBuffer content) throws Exception {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }

}
