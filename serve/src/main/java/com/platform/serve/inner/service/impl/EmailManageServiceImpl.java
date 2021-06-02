package com.platform.serve.inner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.entity.TContentInfo;
import com.platform.entity.TFileInfo;
import com.platform.entity.TInboxMain;
import com.platform.entity.TOutboxMain;
import com.platform.serve.common.constant.Constants;
import com.platform.serve.common.util.CommonUtils;
import com.platform.serve.common.util.SnowFlakeUtil;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.inner.dto.EmailInfoDto;
import com.platform.serve.inner.dto.FileInfoDto;
import com.platform.serve.inner.service.*;
import com.platform.serve.inner.share.service.FileService;
import com.platform.serve.inner.share.service.TContentInfoService;
import com.platform.serve.inner.share.service.TFileInfoService;
import com.platform.serve.inner.share.service.TInboxMainService;
import com.platform.serve.mapper.TContentInfoMapper;
import com.platform.serve.mapper.TFileInfoMapper;
import com.platform.serve.mapper.TInboxMainMapper;
import com.platform.serve.mapper.TOutboxMainMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 邮箱管理类
 *
 * @author lids
 * @version 1.0
 * @date 2021/3/11 22:21
 */
@Slf4j
@Service
public class EmailManageServiceImpl implements EmailManageService {

    @Resource
    private TInboxMainService tInboxMainService;
    @Resource
    private TInboxMainMapper tInboxMainMapper;
    @Resource
    private TContentInfoService tContentInfoService;
    @Resource
    private TOutboxMainMapper tOutboxMainMapper;
    @Resource
    private TContentInfoMapper tContentInfoMapper;
    @Resource
    private TFileInfoService tFileInfoService;
    @Resource
    private TFileInfoMapper tFileInfoMapper;
    @Resource
    private FileService fileService;

    @Transactional
    @Override
    public void saveInboxInfo(List<EmailInfoDto> emailInfoList) {
        if (CollectionUtils.isNotEmpty(emailInfoList)) {
            //邮件主表集合
            List<TInboxMain> inboxMainList = new ArrayList<>();
            //邮件内容集合
            List<TContentInfo> contentInfoList = new ArrayList<>();
            //邮件附件集合
            List<TFileInfo> fileInfoList = new ArrayList<>();
            //附件下载集合
            List<FileInfoDto> fileDownList = new ArrayList<>();

            //处理分发数据
            for (EmailInfoDto emailInfoDto : emailInfoList) {
                try {
                    //发件箱主信息
                    TInboxMain tInboxMain = new TInboxMain();
                    inboxMainList.add(tInboxMain);
                    //邮件内容信息
                    TContentInfo tContentInfo = new TContentInfo();
                    contentInfoList.add(tContentInfo);

                    //创建日期
                    Date date = new Date();

                    //收件箱主信息copy数据
                    BeanUtils.copyProperties(emailInfoDto, tInboxMain);
                    tInboxMain.setEmailId(SnowFlakeUtil.getSnowFlakeId());
                    tInboxMain.setDataStatus(Constants.DATA_STATUE_OK);
                    tInboxMain.setCreator(emailInfoDto.getUserCode());
                    tInboxMain.setCreatDate(date);
                    tInboxMain.setModifiedDate(date);
                    tInboxMain.setModifier(emailInfoDto.getUserCode());

                    //邮件内容信息copy数据
                    BeanUtils.copyProperties(tInboxMain, tContentInfo);
                    tContentInfo.setContentSize(emailInfoDto.getContent() == null ? Constants.SIZE_DEFAULT : (long) emailInfoDto.getContent().length());
                    tContentInfo.setContent(emailInfoDto.getContent());
                    tContentInfo.setEmailType(Constants.EMAIL_TYPE_IN);

                    //附件
                    List<FileInfoDto> fileInfoDtoList = emailInfoDto.getFileInfoDtoList();
                    if (CollectionUtils.isNotEmpty(fileInfoDtoList)) {
                        //存在附件
                        tInboxMain.setContainFile(Constants.CONTAIN_FILE_YES);
                        for (FileInfoDto fileInfoDto : fileInfoDtoList) {
                            fileDownList.add(fileInfoDto);
                            //创建附件信息
                            TFileInfo tFileInfo = new TFileInfo();
                            fileInfoList.add(tFileInfo);
                            //copy附件信息
                            BeanUtils.copyProperties(fileInfoDto, tFileInfo);
                            BeanUtils.copyProperties(tInboxMain, tFileInfo);
                            tFileInfo.setEmailType(Constants.EMAIL_TYPE_IN);
                        }
                    } else {
                        //不存在附件
                        tInboxMain.setContainFile(Constants.CONTAIN_FILE_NO);
                    }
                } catch (BeansException e) {
                    log.error("收件箱邮件：{}处理数据失败！原因：{}", emailInfoDto, e);
                }
            }

            //批量保存：收件箱
            tInboxMainService.saveBatch(inboxMainList);
            //批量保存：邮件内容
            tContentInfoService.saveBatch(contentInfoList);
            //批量保存：附件内容
            tFileInfoService.saveBatch(fileInfoList);
            //批量：存储附件到本地
            fileService.saveDiskFile(fileDownList);
        }

    }


    @Transactional
    @Override
    public void saveOutboxInfo(EmailInfoDto emailInfoDto, String sendStatus, String msg) {
        if (null != emailInfoDto.getEmailId()) {
            QueryWrapper<TOutboxMain> mainQueryWrapper = new QueryWrapper<>();
            mainQueryWrapper.eq("email_id", emailInfoDto.getEmailId());
            mainQueryWrapper.eq("user_code", emailInfoDto.getUserCode());
            TOutboxMain tOutboxMain = new TOutboxMain();
            tOutboxMain.setSendStatus(sendStatus);
            tOutboxMain.setDataStatus(Constants.DATA_STATUE_OK);
            tOutboxMain.setModifiedDate(new Date());
            tOutboxMain.setModifier(emailInfoDto.getUserCode());
            tOutboxMainMapper.update(tOutboxMain, mainQueryWrapper);

        } else {
            //关联数据主键
            emailInfoDto.setEmailId(SnowFlakeUtil.getSnowFlakeId());

            //发件箱主信息
            TOutboxMain tOutboxMain = new TOutboxMain();
            //邮件内容信息
            TContentInfo tContentInfo = new TContentInfo();
            //邮件附件集合
            List<TFileInfo> fileInfoList = new ArrayList<>();

            try {
                //当前日期
                Date date = new Date();

                //发件箱主信息copy数据
                BeanUtils.copyProperties(emailInfoDto, tOutboxMain);
                tOutboxMain.setCreator(emailInfoDto.getUserCode());
                tOutboxMain.setCreatDate(date);
                tOutboxMain.setModifiedDate(date);
                tOutboxMain.setModifier(emailInfoDto.getUserCode());
                tOutboxMain.setSendStatus(sendStatus);
                tOutboxMain.setRemark(msg);

                //邮件内容信息copy数据
                BeanUtils.copyProperties(tOutboxMain, tContentInfo);
                tContentInfo.setContentSize(emailInfoDto.getContent() == null ? Constants.SIZE_DEFAULT : (long) emailInfoDto.getContent().length());
                tContentInfo.setContent(emailInfoDto.getContent());
                tContentInfo.setEmailType(Constants.EMAIL_TYPE_OUT);

                //附件
                List<FileInfoDto> fileInfoDtoList = emailInfoDto.getFileInfoDtoList();
                if (CollectionUtils.isNotEmpty(fileInfoDtoList)) {
                    //存在附件
                    tOutboxMain.setContainFile(Constants.CONTAIN_FILE_YES);
                    for (FileInfoDto fileInfoDto : fileInfoDtoList) {
                        //创建附件信息
                        TFileInfo tFileInfo = new TFileInfo();
                        fileInfoList.add(tFileInfo);
                        //copy数据
                        BeanUtils.copyProperties(fileInfoDto, tFileInfo);
                        BeanUtils.copyProperties(tOutboxMain, tFileInfo);
                        tFileInfo.setEmailType(Constants.EMAIL_TYPE_OUT);
                    }
                } else {
                    //不存在附件
                    tOutboxMain.setContainFile(Constants.CONTAIN_FILE_NO);
                }

                //保存数据
                tOutboxMainMapper.insert(tOutboxMain);
                tContentInfoMapper.insert(tContentInfo);
                tFileInfoService.saveBatch(fileInfoList);

            } catch (Exception e) {
                //throw new CheckException("写入数据失败！");
                log.error("发件箱保存数据失败！原因：{}", e);
            }
        }
    }


    @Override
    public Page<EmailInfoDto> selectOutboxEmailList(EmailInfoDto emailInfoDto) {
        //校验数据
        CommonUtils.checkStringException(emailInfoDto.getUserCode(), "请输入用户账号！");
        CommonUtils.checkStringException(emailInfoDto.getSendStatus(), "请输入查询邮箱类型！");
        Page<EmailInfoDto> page = new Page<>(emailInfoDto.getPage(), emailInfoDto.getPageSize());
        return tOutboxMainMapper.selectOutboxEmailList(page, emailInfoDto);
    }


    @Override
    public Page<EmailInfoDto> selectInboxEmailList(EmailInfoDto emailInfoDto) {
        //校验数据
        CommonUtils.checkStringException(emailInfoDto.getUserCode(), "请输入用户账号！");

        Page<EmailInfoDto> result = new Page<>();
        IPage<TInboxMain> page = new Page<>(emailInfoDto.getPage(), emailInfoDto.getPageSize());
        LambdaQueryWrapper<TInboxMain> wrapper = new LambdaQueryWrapper<TInboxMain>()
                .eq(StringUtils.isNoneEmpty(emailInfoDto.getAccount()), TInboxMain::getAccount, emailInfoDto.getAccount())
                .eq(StringUtils.isNoneEmpty(emailInfoDto.getFromAddress()),TInboxMain::getFromAddress,emailInfoDto.getFromAddress())
                .eq(StringUtils.isNoneEmpty(emailInfoDto.getReceivers()),TInboxMain::getReceivers,emailInfoDto.getReceivers())
                .like(StringUtils.isNoneEmpty(emailInfoDto.getSubject()),TInboxMain::getSubject,emailInfoDto.getSubject())
                .eq(TInboxMain::getDataStatus,"1");
        IPage<TInboxMain> tInboxMainPage = tInboxMainMapper.selectPage(page, wrapper);
        List<TInboxMain> records = tInboxMainPage.getRecords();

        List<EmailInfoDto> list = new ArrayList<>();

        for (TInboxMain record : records) {
            EmailInfoDto emailInfoDto1 = new EmailInfoDto();
            BeanUtils.copyProperties(record,emailInfoDto1);
            list.add(emailInfoDto1);
        }
        result.setRecords(list);

        return result;
    }


    @Override
    public EmailInfoDto selectOutboxInfo(EmailInfoDto emailInfoDto) {
        //校验数据
        CommonUtils.checkStringException(emailInfoDto.getUserCode(), "请输入用户账号！");
        CommonUtils.checkLongException(emailInfoDto.getEmailId(), "邮件关联ID！");
        EmailInfoDto reDto = null;
        List<EmailInfoDto> dtoList = tOutboxMainMapper.selectOutboxEmailList(emailInfoDto);
        if (CollectionUtils.isNotEmpty(dtoList)) {
            reDto = dtoList.get(0);

            //查询附件信息
            reDto.setFileInfoDtoList(getTFileInfoByEmailId(emailInfoDto));
        }
        return reDto;
    }

    /**
     * 根据邮件关联ID查询邮件附件
     *
     * @param emailInfoDto
     * @return
     */
    private List<FileInfoDto> getTFileInfoByEmailId(EmailInfoDto emailInfoDto) {
        List<FileInfoDto> reFileInfoDtos = null;
        //查询附件信息
        QueryWrapper<TFileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", emailInfoDto.getUserCode());
        queryWrapper.eq("email_id", emailInfoDto.getEmailId());
        queryWrapper.eq("data_status", Constants.DATA_STATUE_OK);
        List<TFileInfo> tFileInfos = tFileInfoMapper.selectList(queryWrapper);
        //判断是否含有附件
        if (CollectionUtils.isNotEmpty(tFileInfos)) {
            //处理附件数据
            reFileInfoDtos = new ArrayList<>();
            for (TFileInfo tFileInfo : tFileInfos) {
                FileInfoDto fileInfoDto = new FileInfoDto();
                reFileInfoDtos.add(fileInfoDto);
                BeanUtils.copyProperties(tFileInfo, fileInfoDto);
            }
        }
        return reFileInfoDtos;
    }

    @Override
    public EmailInfoDto selectInboxInfo(EmailInfoDto emailInfoDto) {
        //校验数据
        CommonUtils.checkStringException(emailInfoDto.getUserCode(), "请输入用户账号！");
        CommonUtils.checkLongException(emailInfoDto.getEmailId(), "邮件关联ID！");
        EmailInfoDto reDto = null;
        List<EmailInfoDto> dtoList = tInboxMainMapper.selectInboxEmailList(emailInfoDto);
        if (CollectionUtils.isNotEmpty(dtoList)) {
            reDto = dtoList.get(0);

            QueryWrapper<TInboxMain> mainQueryWrapper = new QueryWrapper<>();
            mainQueryWrapper.eq("email_id", reDto.getEmailId());
            mainQueryWrapper.eq("user_code", reDto.getUserCode());
            TInboxMain tInboxMain = new TInboxMain();
            tInboxMain.setUserSeen(Constants.IS_SEEN_YES);
            tInboxMainMapper.update(tInboxMain, mainQueryWrapper);

            //查询附件信息
            reDto.setFileInfoDtoList(getTFileInfoByEmailId(emailInfoDto));
        }
        return reDto;
    }

    @Transactional
    @Override
    public void delEmail(EmailInfoDto emailInfoDto) {
        //校验数据
        CommonUtils.checkStringException(emailInfoDto.getUserCode(), "请输入用户账号！");
        CommonUtils.checkLongException(emailInfoDto.getEmailId(), "邮件关联ID！");

        //修改收件箱状态
        QueryWrapper<TInboxMain> tInboxMainWrapper = new QueryWrapper<>();
        tInboxMainWrapper.eq("email_id", emailInfoDto.getEmailId());
        tInboxMainWrapper.eq("user_code", emailInfoDto.getUserCode());
        tInboxMainWrapper.ne("data_status", Constants.DATA_STATUE_NO);

        TInboxMain tInboxMain = new TInboxMain();
        tInboxMain.setModifier(emailInfoDto.getUserCode());
        tInboxMain.setModifiedDate(new Date());
        tInboxMain.setDataStatus(Constants.DATA_STATUE_NO);
        tInboxMainMapper.update(tInboxMain, tInboxMainWrapper);


        //修改发件箱状态
        QueryWrapper<TOutboxMain> tOutboxMainWrapper = new QueryWrapper<>();
        tOutboxMainWrapper.eq("email_id", emailInfoDto.getEmailId());
        tOutboxMainWrapper.eq("user_code", emailInfoDto.getUserCode());
        tOutboxMainWrapper.ne("data_status", Constants.DATA_STATUE_NO);

        TOutboxMain tOutboxMain = new TOutboxMain();
        tOutboxMain.setModifier(emailInfoDto.getUserCode());
        tOutboxMain.setModifiedDate(new Date());
        tOutboxMain.setDataStatus(Constants.DATA_STATUE_NO);
        tOutboxMainMapper.update(tOutboxMain, tOutboxMainWrapper);

        //修改附件状态
        QueryWrapper<TFileInfo> tFileInfoWrapper = new QueryWrapper<>();
        tFileInfoWrapper.eq("email_id", emailInfoDto.getEmailId());
        tFileInfoWrapper.eq("user_code", emailInfoDto.getUserCode());
        tFileInfoWrapper.ne("data_status", Constants.DATA_STATUE_NO);

        TFileInfo tFileInfo = new TFileInfo();
        tFileInfo.setModifier(emailInfoDto.getUserCode());
        tFileInfo.setModifiedDate(new Date());
        tFileInfo.setDataStatus(Constants.DATA_STATUE_NO);
        tFileInfoMapper.update(tFileInfo, tFileInfoWrapper);

        //修改邮件内容状态
        QueryWrapper<TContentInfo> tContentInfoWrapper = new QueryWrapper<>();
        tContentInfoWrapper.eq("email_id", emailInfoDto.getEmailId());
        tContentInfoWrapper.eq("user_code", emailInfoDto.getUserCode());
        tContentInfoWrapper.ne("data_status", Constants.DATA_STATUE_NO);

        TContentInfo tContentInfo = new TContentInfo();
        tContentInfo.setModifier(emailInfoDto.getUserCode());
        tContentInfo.setModifiedDate(new Date());
        tContentInfo.setDataStatus(Constants.DATA_STATUE_NO);
        tContentInfoMapper.update(tContentInfo, tContentInfoWrapper);


    }


}
