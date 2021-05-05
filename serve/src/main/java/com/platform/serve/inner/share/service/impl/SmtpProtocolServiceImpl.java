package com.platform.serve.inner.share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.entity.BscSmtpProtocolT;

import com.platform.serve.common.constant.Constants;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.inner.share.service.SmtpProtocolService;
import com.platform.serve.mapper.BscSmtpProtocolTMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 邮箱协议Service
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/3 18:22
 */
@Service
public class SmtpProtocolServiceImpl implements SmtpProtocolService {
    @Resource
    private BscSmtpProtocolTMapper bscSmtpProtocolTMapper;

    @Override
    public BscSmtpProtocolT getSmtpProtocolByMailboxSuffix(BscSmtpProtocolT bscSmtpProtocolT) {
        BscSmtpProtocolT reBscSmtpProtocolT = null;
        String mailboxSuffix = bscSmtpProtocolT.getMailboxSuffix();
        if (StringUtils.isEmpty(mailboxSuffix)) {
            throw new CheckException("请输入邮箱后缀等参数！");

        }

        //查询数据
        QueryWrapper<BscSmtpProtocolT> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mailbox_suffix", mailboxSuffix);
        queryWrapper.eq(StringUtils.isNotEmpty(bscSmtpProtocolT.getMailType()), "mail_type", bscSmtpProtocolT.getMailType());
        queryWrapper.eq(StringUtils.isNotEmpty(bscSmtpProtocolT.getMailCode()), "mail_code", bscSmtpProtocolT.getMailCode());
        queryWrapper.eq("data_status", Constants.DATA_STATUE_OK);
        queryWrapper.orderByDesc("modified_date");
        List<BscSmtpProtocolT> bscSmtpProtocolTList = bscSmtpProtocolTMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(bscSmtpProtocolTList)) {
            reBscSmtpProtocolT = bscSmtpProtocolTList.get(0);
        }
        return reBscSmtpProtocolT;

    }

    @Override
    public List<BscSmtpProtocolT> getBscSmtpProtocolTByMailboxSuffix(String mailboxSuffix) {
        QueryWrapper<BscSmtpProtocolT> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("data_status", Constants.DATA_STATUE_OK);
        queryWrapper.eq("mailbox_suffix", mailboxSuffix);
        return bscSmtpProtocolTMapper.selectList(queryWrapper);
    }

}

