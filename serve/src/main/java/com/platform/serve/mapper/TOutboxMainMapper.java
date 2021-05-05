package com.platform.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.entity.TOutboxMain;
import com.platform.serve.inner.dto.EmailInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 发件箱主表Mapper
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/11 22:42
 */
@Mapper
public interface TOutboxMainMapper extends BaseMapper<TOutboxMain> {
    /**
     * 查询发件箱信息：分页
     *
     * @param page
     * @param emailInfoDto
     * @return
     */
    Page<EmailInfoDto> selectOutboxEmailList(Page page, @Param(value = "dto") EmailInfoDto emailInfoDto);

    /**
     * 查询发件箱信息
     *
     * @param emailInfoDto
     * @return
     */
    List<EmailInfoDto> selectOutboxEmailList(@Param(value = "dto") EmailInfoDto emailInfoDto);

}
