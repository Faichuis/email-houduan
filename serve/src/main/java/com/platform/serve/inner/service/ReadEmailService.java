package com.platform.serve.inner.service;

import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;

/**
 * @author lids
 * @version 1.0
 * @date 2020/12/27 16:40
 */
public interface ReadEmailService {


    /**
     * 判断账号是否可用
     *
     * @param dto
     */
    Boolean judgeMailUserIsNormal(UserMailBoxInfoDto dto);

    /**
     * 读取邮件
     *
     * @param dto
     */
    void readEmail(UserMailBoxInfoDto dto);

    /**
     * 读取所有关联的邮件
     *
     * @param dto
     */
    void readEmailByUserCode(UserMailBoxInfoDto dto);
}
