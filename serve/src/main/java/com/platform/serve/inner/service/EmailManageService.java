package com.platform.serve.inner.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.serve.inner.dto.EmailInfoDto;

import java.util.List;

/**
 * 邮箱管理类
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/11 22:21
 */
public interface EmailManageService {

    /**
     * 保存收件箱信息
     *
     * @param emailInfoList
     */
    void saveInboxInfo(List<EmailInfoDto> emailInfoList);

    /**
     * 保存发件箱信息
     *
     * @param emailInfoDto
     * @param sendStatus
     * @param msg
     */
    void saveOutboxInfo(EmailInfoDto emailInfoDto, String sendStatus, String msg);

    /**
     * 查询发件箱集合：分页
     *
     * @param emailInfoDto
     * @return
     */
    Page<EmailInfoDto> selectOutboxEmailList(EmailInfoDto emailInfoDto);

    /**
     * 查询收件箱集合：分页
     *
     * @param emailInfoDto
     * @return
     */
    Page<EmailInfoDto> selectInboxEmailList(EmailInfoDto emailInfoDto);

    /**
     * 查询发件箱详细信息
     *
     * @param emailInfoDto
     * @return
     */
    EmailInfoDto selectOutboxInfo(EmailInfoDto emailInfoDto);

    /**
     * 查询收件箱详细信息
     *
     * @param emailInfoDto
     * @return
     */
    EmailInfoDto selectInboxInfo(EmailInfoDto emailInfoDto);


    /**
     * 删除邮件
     *
     * @param emailInfoDto
     */
    void delEmail(EmailInfoDto emailInfoDto);


}
