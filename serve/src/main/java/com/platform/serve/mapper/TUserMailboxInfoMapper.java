package com.platform.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.TUserMailboxInfo;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lids
 * @version 1.0
 * @date 2021/1/3 21:44
 */
@Mapper
public interface TUserMailboxInfoMapper extends BaseMapper<TUserMailboxInfo> {

    /**
     * 根据条件查询个人邮箱及其链接信息
     *
     * @param userCode
     * @param account
     * @param mailType
     * @param mailboxSuffix
     * @param dataStatus
     * @return
     */
    List<UserMailBoxInfoDto> getUserMailBoxInfoList(@Param(value = "userCode") String userCode,
                                                    @Param(value = "account") String account,
                                                    @Param(value = "mailType") String mailType,
                                                    @Param(value = "mailboxSuffix") String mailboxSuffix,
                                                    @Param(value = "dataStatus") String dataStatus);
}
