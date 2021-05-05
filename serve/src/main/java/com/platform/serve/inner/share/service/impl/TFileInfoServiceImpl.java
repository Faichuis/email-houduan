package com.platform.serve.inner.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.entity.TFileInfo;
import com.platform.serve.inner.share.service.TFileInfoService;
import com.platform.serve.mapper.TFileInfoMapper;
import org.springframework.stereotype.Service;

/**
 * 邮件附件servce
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/11 22:41
 */
@Service
public class TFileInfoServiceImpl extends ServiceImpl<TFileInfoMapper, TFileInfo> implements TFileInfoService {
}
