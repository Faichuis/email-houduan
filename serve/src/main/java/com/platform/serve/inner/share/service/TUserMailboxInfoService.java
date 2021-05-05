package com.platform.serve.inner.share.service;

import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;

import java.util.List;

/**
 * 用户关联邮箱信息Service
 *
 * @Author lds
 * @Date 2021/4/13 11:07
 */
public interface TUserMailboxInfoService {
    /**
     * 查询用户关联邮箱
     *
     * @param userMailBoxInfoDto
     * @return
     */
    List<UserMailBoxInfoDto> selectMailbox(UserMailBoxInfoDto userMailBoxInfoDto);

    /**
     * 用户关联邮箱
     *
     * @param userMailBoxInfoDto
     */
    void saveMailbox(UserMailBoxInfoDto userMailBoxInfoDto);

    /**
     * 修改用户关联邮箱
     *
     * @param userMailBoxInfoDto
     */
    void updateMailbox(UserMailBoxInfoDto userMailBoxInfoDto);

    /**
     * 用户解除关联邮箱
     *
     * @param userMailBoxInfoDto
     */
    void delMailbox(UserMailBoxInfoDto userMailBoxInfoDto);

}
