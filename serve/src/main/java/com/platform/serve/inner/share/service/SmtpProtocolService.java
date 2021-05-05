package com.platform.serve.inner.share.service;

import com.platform.entity.BscSmtpProtocolT;

import java.util.List;

/**
 * 邮箱协议Service
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/3 18:22
 */
public interface SmtpProtocolService {

    BscSmtpProtocolT getSmtpProtocolByMailboxSuffix(BscSmtpProtocolT bscSmtpProtocolT);

    List<BscSmtpProtocolT> getBscSmtpProtocolTByMailboxSuffix(String mailboxSuffix);
}
