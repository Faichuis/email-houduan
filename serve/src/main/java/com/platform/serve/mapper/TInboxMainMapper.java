package com.platform.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.entity.TInboxMain;
import com.platform.serve.inner.dto.EmailInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收件箱主表Mapper
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/11 22:42
 */
@Mapper
public interface TInboxMainMapper extends BaseMapper<TInboxMain> {

    /**
     * 根据用户编码和邮箱账号查询服务器ID
     *
     * @param userCode
     * @param account
     * @param dataStatus
     * @return
     */
    List<String> getMessageIdListByAccount(@Param(value = "userCode") String userCode,
                                           @Param(value = "account") String account,
                                           @Param(value = "dataStatus") String dataStatus);


    /**
     * 查询收件箱信息：分页
     *
     * @param page
     * @param emailInfoDto
     * @return
     */
    Page<EmailInfoDto> selectInboxEmailList(Page page, @Param(value = "dto") EmailInfoDto emailInfoDto);

    /**
     * 查询收件箱信息
     *
     * @param emailInfoDto
     * @return
     */
    List<EmailInfoDto> selectInboxEmailList(@Param(value = "dto") EmailInfoDto emailInfoDto);
}
