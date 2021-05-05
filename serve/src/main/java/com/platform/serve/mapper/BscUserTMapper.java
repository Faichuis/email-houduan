package com.platform.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.BscUserT;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息Mapper
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/11 22:42
 */
@Mapper
public interface BscUserTMapper extends BaseMapper<BscUserT> {

    /**
     * 用户信息验证关键字段是否唯一
     *
     * @param userCode
     * @param loginPhone
     * @param loginEmail
     * @param dataStatus
     * @return
     */
    List<BscUserT> checkUniquenessRegisterData(@Param(value = "userCode") String userCode,
                                               @Param(value = "loginPhone") String loginPhone,
                                               @Param(value = "loginEmail") String loginEmail,
                                               @Param(value = "dataStatus") String dataStatus);

}
