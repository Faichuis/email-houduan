package com.platform.serve.inner.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.entity.TContentInfo;
import com.platform.serve.inner.share.service.TContentInfoService;
import com.platform.serve.mapper.TContentInfoMapper;
import org.springframework.stereotype.Service;


/**
 * 邮件内容servce
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/11 22:41
 */
@Service
public class TContentInfoServiceImpl extends ServiceImpl<TContentInfoMapper, TContentInfo> implements TContentInfoService {
}
