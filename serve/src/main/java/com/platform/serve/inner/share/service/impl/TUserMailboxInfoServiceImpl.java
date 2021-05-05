package com.platform.serve.inner.share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.entity.BscSmtpProtocolT;
import com.platform.entity.TUserMailboxInfo;
import com.platform.serve.common.constant.Constants;
import com.platform.serve.common.util.CommonUtils;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.inner.service.ReadEmailService;
import com.platform.serve.inner.share.dto.UserMailBoxInfoDto;
import com.platform.serve.inner.share.service.SmtpProtocolService;
import com.platform.serve.inner.share.service.TUserMailboxInfoService;
import com.platform.serve.mapper.TUserMailboxInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户关联邮箱信息Service
 *
 * @Author lds
 * @Date 2021/4/13 11:07
 */
@Slf4j
@Service
public class TUserMailboxInfoServiceImpl implements TUserMailboxInfoService {

    @Resource
    private TUserMailboxInfoMapper tUserMailboxInfoMapper;
    @Resource
    private SmtpProtocolService smtpProtocolService;
    @Lazy
    @Resource
    private ReadEmailService readEmailService;


    @Override
    public List<UserMailBoxInfoDto> selectMailbox(UserMailBoxInfoDto userMailBoxInfoDto) {
        //校验数据
        CommonUtils.checkStringException(userMailBoxInfoDto.getUserCode(), "请输入用户编码");
        //查询数据
        QueryWrapper<TUserMailboxInfo> mailboxInfoQueryWrapper = new QueryWrapper<>();
        mailboxInfoQueryWrapper.eq("user_code", userMailBoxInfoDto.getUserCode());
        mailboxInfoQueryWrapper.ne("data_status", Constants.DATA_STATUE_NO);
        List<TUserMailboxInfo> tUserMailboxInfoList = tUserMailboxInfoMapper.selectList(mailboxInfoQueryWrapper);
        //处理数据
        List<UserMailBoxInfoDto> reList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tUserMailboxInfoList)) {
            tUserMailboxInfoList.stream().forEach(s -> {
                UserMailBoxInfoDto reDto = new UserMailBoxInfoDto();
                BeanUtils.copyProperties(s, reDto);
                reDto.setUserMailBoxId(s.getId());
                reList.add(reDto);
            });
        }
        return reList;
    }

    @Transactional
    @Override
    public void saveMailbox(UserMailBoxInfoDto userMailBoxInfoDto) {
        //校验数据
        checkSaveMailbox(userMailBoxInfoDto);
        //查询数据
        QueryWrapper<TUserMailboxInfo> mailboxInfoQueryWrapper = new QueryWrapper<>();
        mailboxInfoQueryWrapper.eq("user_code", userMailBoxInfoDto.getUserCode());
        mailboxInfoQueryWrapper.eq("account", userMailBoxInfoDto.getAccount());
        List<TUserMailboxInfo> mailboxInfoList = tUserMailboxInfoMapper.selectList(mailboxInfoQueryWrapper);
        if (CollectionUtils.isNotEmpty(mailboxInfoList)) {
            //调取修改方法
            updateMailbox(userMailBoxInfoDto);
        } else {
            //copy数据
            TUserMailboxInfo tUserMailboxInfo = copySaveTUserMailboxInfo(userMailBoxInfoDto);
            try {
                //插入数据
                tUserMailboxInfoMapper.insert(tUserMailboxInfo);
            } catch (Exception e) {
                log.error("用户关联邮箱失败", e);
                throw new CheckException("用户关联邮箱失败");
            }
        }
    }

    /**
     * 校验登录数据
     *
     * @param dto
     */
    private void checkSaveMailbox(UserMailBoxInfoDto dto) {
        CommonUtils.checkStringException(dto.getUserCode(), "请输入用户编码");
        //CommonUtils.checkStringException(dto.getUserName(), "请输入用户名");
        CommonUtils.checkStringException(dto.getAccount(), "请输入邮箱账号");
        CommonUtils.checkStringException(dto.getPassword(), "请输入邮箱密码");
        String account = dto.getAccount();
        if (account.contains("@")) {
            String mailboxSuffix = account.substring(account.lastIndexOf("@"));
            dto.setMailboxSuffix(mailboxSuffix);
            //查询服务器信息
            List<BscSmtpProtocolT> bscSmtpProtocolTList = smtpProtocolService.getBscSmtpProtocolTByMailboxSuffix(mailboxSuffix);
            if (bscSmtpProtocolTList == null || bscSmtpProtocolTList.size() != 2) {
                throw new CheckException("未收录的邮箱，请勿绑定！");
            }
        } else {
            CommonUtils.checkStringException(dto.getAccount(), "请输入正确的邮箱账号");
        }
        //验证账号数据
        UserMailBoxInfoDto userMailBoxInfoDto = new UserMailBoxInfoDto();
        userMailBoxInfoDto.setMailboxSuffix(dto.getMailboxSuffix());
        userMailBoxInfoDto.setAccount(dto.getAccount());
        userMailBoxInfoDto.setPassword(dto.getPassword());
        Boolean judgeMailUserIsNormal = readEmailService.judgeMailUserIsNormal(userMailBoxInfoDto);
        if (judgeMailUserIsNormal) {
            dto.setDataStatus(Constants.DATA_STATUE_OK);
        } else {
            //未激活
            dto.setDataStatus(Constants.DATA_STATUE_LOSE);
        }
    }

    /**
     * copy用户关联邮箱数据
     *
     * @param dto
     */
    private TUserMailboxInfo copySaveTUserMailboxInfo(UserMailBoxInfoDto dto) {
        TUserMailboxInfo tUserMailboxInfo = new TUserMailboxInfo();
        tUserMailboxInfo.setUserCode(dto.getUserCode());
        tUserMailboxInfo.setUserName(dto.getUserName());
        tUserMailboxInfo.setAccount(dto.getAccount());
        tUserMailboxInfo.setPassword(dto.getPassword());
        tUserMailboxInfo.setMailboxSuffix(dto.getMailboxSuffix());
        tUserMailboxInfo.setRemark(dto.getRemark());
        tUserMailboxInfo.setDataStatus(dto.getDataStatus());
        Date date = new Date();
        tUserMailboxInfo.setCreatDate(date);
        tUserMailboxInfo.setModifiedDate(date);
        tUserMailboxInfo.setCreator(dto.getUserCode());
        tUserMailboxInfo.setModifier(dto.getUserCode());
        //目前不支持选择协议，使用默认值
        tUserMailboxInfo.setStoreCode("pop3");
        tUserMailboxInfo.setTransportCode("smtp");
        return tUserMailboxInfo;
    }


    @Transactional
    @Override
    public void updateMailbox(UserMailBoxInfoDto userMailBoxInfoDto) {
        //校验数据
        checkUpdateMailbox(userMailBoxInfoDto);
        //查询数据
        QueryWrapper<TUserMailboxInfo> mailboxInfoQueryWrapper = new QueryWrapper<>();
        mailboxInfoQueryWrapper.eq("user_code", userMailBoxInfoDto.getUserCode());
        mailboxInfoQueryWrapper.eq("account", userMailBoxInfoDto.getAccount());
        List<TUserMailboxInfo> mailboxInfoList = tUserMailboxInfoMapper.selectList(mailboxInfoQueryWrapper);
        if (CollectionUtils.isNotEmpty(mailboxInfoList)) {
            //copy数据
            TUserMailboxInfo tUserMailboxInfo = copyUpdateTUserMailboxInfo(userMailBoxInfoDto);
            try {
                //修改数据
                tUserMailboxInfoMapper.update(tUserMailboxInfo, mailboxInfoQueryWrapper);
            } catch (Exception e) {
                log.error("修改用户关联邮箱失败", e);
                throw new CheckException("修改用户关联邮箱失败");
            }
        } else {
            throw new CheckException("未查询到邮箱：" + userMailBoxInfoDto.getAccount() + "的绑定数据，请刷新重试！");
        }

    }

    /**
     * 校验登录数据
     *
     * @param dto
     */
    private void checkUpdateMailbox(UserMailBoxInfoDto dto) {
        CommonUtils.checkStringException(dto.getUserCode(), "请输入用户编码");
        CommonUtils.checkStringException(dto.getAccount(), "请输入邮箱账号");

        if (StringUtils.isNotEmpty(dto.getPassword())) {
            //获取邮箱后缀
            String account = dto.getAccount();
            if (account.contains("@")) {
                String mailboxSuffix = account.substring(account.lastIndexOf("@"));
                dto.setMailboxSuffix(mailboxSuffix);
            } else {
                CommonUtils.checkStringException(dto.getAccount(), "请输入正确的邮箱账号");
            }
            //验证账号数据
            UserMailBoxInfoDto userMailBoxInfoDto = new UserMailBoxInfoDto();
            userMailBoxInfoDto.setMailboxSuffix(dto.getMailboxSuffix());
            userMailBoxInfoDto.setAccount(dto.getAccount());
            userMailBoxInfoDto.setPassword(dto.getPassword());
            Boolean judgeMailUserIsNormal = readEmailService.judgeMailUserIsNormal(userMailBoxInfoDto);
            if (judgeMailUserIsNormal) {
                dto.setDataStatus(Constants.DATA_STATUE_OK);
            } else {
                //未激活
                dto.setDataStatus(Constants.DATA_STATUE_LOSE);
            }
        }
    }

    /**
     * copy用户关联邮箱数据
     *
     * @param dto
     */
    private TUserMailboxInfo copyUpdateTUserMailboxInfo(UserMailBoxInfoDto dto) {
        TUserMailboxInfo tUserMailboxInfo = new TUserMailboxInfo();
        //tUserMailboxInfo.setUserCode(dto.getUserCode());
        //tUserMailboxInfo.setAccount(dto.getAccount());
        tUserMailboxInfo.setPassword(dto.getPassword());
        tUserMailboxInfo.setRemark(dto.getRemark());
        tUserMailboxInfo.setModifiedDate(new Date());
        tUserMailboxInfo.setModifier(dto.getUserCode());
        tUserMailboxInfo.setDataStatus(dto.getDataStatus());
        return tUserMailboxInfo;
    }

    @Transactional
    @Override
    public void delMailbox(UserMailBoxInfoDto userMailBoxInfoDto) {
        //校验数据
        CommonUtils.checkStringException(userMailBoxInfoDto.getUserCode(), "请输入用户编码");
        CommonUtils.checkStringException(userMailBoxInfoDto.getAccount(), "请输入邮箱账号");

        TUserMailboxInfo tUserMailboxInfo = new TUserMailboxInfo();
        //逻辑删除
        tUserMailboxInfo.setDataStatus(Constants.UNACTIVAT_STATUE);

        QueryWrapper<TUserMailboxInfo> tUserMailboxInfoQueryWrapper = new QueryWrapper<>();
        tUserMailboxInfoQueryWrapper.eq("user_code", userMailBoxInfoDto.getUserCode());
        tUserMailboxInfoQueryWrapper.eq("account", userMailBoxInfoDto.getAccount());
        tUserMailboxInfoQueryWrapper.ne("data_status", Constants.UNACTIVAT_STATUE);
        //修改数据数据
        tUserMailboxInfoMapper.update(tUserMailboxInfo, tUserMailboxInfoQueryWrapper);

        //TODO 修改收件箱，发件箱，草稿箱，附件，内容等信息

    }

}
