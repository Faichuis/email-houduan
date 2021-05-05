package com.platform.serve.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.entity.BscUserT;
import com.platform.serve.common.constant.Constants;
import com.platform.serve.common.util.CommonUtils;
import com.platform.serve.config.exception.CheckException;
import com.platform.serve.mapper.BscUserTMapper;
import com.platform.serve.user.dto.UserInfoDto;
import com.platform.serve.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 用户信息（注册、登录、忘记密码、注销等）业务处理Service
 *
 * @Author lds
 * @Date 2021/4/12 9:28
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private BscUserTMapper bscUserTMapper;


    @Transactional
    @Override
    public void userRegister(UserInfoDto userInfoDto) {
        //校验数据
        checkUserRegister(userInfoDto);

        //验证关键字段是否唯一：userCode唯一、手机号唯一、邮箱账号唯一
        checkUniquenessRegisterData(userInfoDto.getUserCode(), userInfoDto.getLoginPhone(), userInfoDto.getLoginEmail());

        //copy数据
        BscUserT bscUserT = copyRegisterData(userInfoDto);

        //写入数据
        try {
            bscUserTMapper.insert(bscUserT);
        } catch (Exception e) {
            log.error("注册用户失败", e);
            throw new CheckException("注册用户失败，请重试！");
        }

    }

    /**
     * 校验登录数据
     *
     * @param dto
     */
    private void checkUserRegister(UserInfoDto dto) {
        CommonUtils.checkStringException(dto.getUserCode(), "请输入用户编码");
        CommonUtils.checkStringException(dto.getUserName(), "请输入用户名");
        CommonUtils.checkStringException(dto.getUserPwd(), "请输入用户密码");
        CommonUtils.checkStringException(dto.getLoginPhone(), "请输入注册手机号");
        CommonUtils.checkStringException(dto.getLoginEmail(), "请输入注册邮箱号");

    }

    /**
     * 验证关键字段是否唯一
     *
     * @param userCode
     * @param loginPhone
     * @param loginEmail
     */
    private void checkUniquenessRegisterData(String userCode, String loginPhone, String loginEmail) {
        //userCode唯一、手机号唯一、邮箱账号唯一
        List<BscUserT> userTList = bscUserTMapper.checkUniquenessRegisterData(userCode, loginPhone, loginEmail, Constants.DATA_STATUE_OK);
        if (CollectionUtils.isNotEmpty(userTList)) {
            for (BscUserT bscUserT : userTList) {
                if (StringUtils.isNotEmpty(userCode) && userCode.equals(bscUserT.getUserCode())) {
                    StringBuilder stringBuilder = new StringBuilder("用户编码：").append(userCode).append("已经使用。");
                    if (Constants.ACTIVAT_STATUE.equals(bscUserT.getActivateStatus())) {
                        stringBuilder.append("请直接登录！");
                    } else if (Constants.UNACTIVAT_STATUE.equals(bscUserT.getActivateStatus())) {
                        stringBuilder.append("请前往激活！");
                    }
                    throw new CheckException(stringBuilder.toString());
                }
                if (StringUtils.isNotEmpty(loginPhone) && loginPhone.equals(bscUserT.getLoginPhone())) {
                    StringBuilder stringBuilder = new StringBuilder("该手机号：").append(loginPhone).append("已经注册。");
                    if (Constants.ACTIVAT_STATUE.equals(bscUserT.getActivateStatus())) {
                        stringBuilder.append("请直接登录！");
                    } else if (Constants.UNACTIVAT_STATUE.equals(bscUserT.getActivateStatus())) {
                        stringBuilder.append("请前往激活！");
                    }
                    throw new CheckException(stringBuilder.toString());
                }
                if (StringUtils.isNotEmpty(loginEmail) && loginEmail.equals(bscUserT.getLoginEmail())) {
                    StringBuilder stringBuilder = new StringBuilder("该邮箱：").append(loginEmail).append("已经注册。");
                    if (Constants.ACTIVAT_STATUE.equals(bscUserT.getActivateStatus())) {
                        stringBuilder.append("请直接登录！");
                    } else if (Constants.UNACTIVAT_STATUE.equals(bscUserT.getActivateStatus())) {
                        stringBuilder.append("请前往激活！");
                    }
                    throw new CheckException(stringBuilder.toString());
                }

            }
        }
    }


    /**
     * copy数据
     *
     * @param dto
     */
    private BscUserT copyRegisterData(UserInfoDto dto) {
        BscUserT bscUserT = new BscUserT();
        bscUserT.setUserCode(dto.getUserCode());
        bscUserT.setUserName(dto.getUserName());
        //TODO 加密
        bscUserT.setUserPwd(encryptionUserPwd(dto.getUserPwd()));
        bscUserT.setLoginPhone(dto.getLoginPhone());
        bscUserT.setLoginEmail(dto.getLoginEmail());
        //默认没有密保问题，之后扩展
        bscUserT.setSecurityQuestion(Constants.SECURITY_QUESTION_NO);
        //默认没有会员等级，之后扩展
        bscUserT.setMember(Constants.UNMEMBER);
        ////默认未激活
        //bscUserT.setDataStatus(Constants.UNACTIVAT_STATUE);
        bscUserT.setDataStatus(Constants.DATA_STATUE_OK);
        bscUserT.setActivateStatus(Constants.ACTIVAT_STATUE);
        bscUserT.setRemark(dto.getRemark());
        bscUserT.setCreator(dto.getUserCode());
        bscUserT.setModifier(dto.getUserCode());
        Date date = new Date();
        bscUserT.setActivateDate(date);
        bscUserT.setCreatDate(date);
        bscUserT.setModifiedDate(date);
        return bscUserT;
    }


    @Override
    public UserInfoDto loginUser(UserInfoDto userInfoDto) {
        //校验数据
        CommonUtils.checkStringException(userInfoDto.getUserCode(), "请输入要登录用户编码");
        CommonUtils.checkStringException(userInfoDto.getUserPwd(), "请输入要登录用户密码");
        QueryWrapper<BscUserT> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userInfoDto.getUserCode());
        queryWrapper.eq("data_status", Constants.DATA_STATUE_OK);
        List<BscUserT> userTList = bscUserTMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(userTList)) {
            BscUserT bscUserT = userTList.get(0);
            if (bscUserT != null && bscUserT.getUserPwd().equals(encryptionUserPwd(userInfoDto.getUserPwd()))) {
                UserInfoDto reUser = new UserInfoDto();
                reUser.setUserCode(bscUserT.getUserCode());
                reUser.setUserName(bscUserT.getUserName());
                reUser.setLoginPhone(bscUserT.getLoginPhone());
                reUser.setLoginEmail(bscUserT.getLoginEmail());
                reUser.setRemark(bscUserT.getRemark());
                reUser.setDataStatus(bscUserT.getDataStatus());
                return reUser;
            } else {
                throw new CheckException("用户密码不正确!");
            }
        } else {
            throw new CheckException("未录入的用户!");
        }
    }


    @Transactional
    @Override
    public void updateUserInfo(UserInfoDto userInfoDto) {
        //校验数据
        CommonUtils.checkStringException(userInfoDto.getUserCode(), "请选择要修改的用户编码");

        //数据状态判断
        BscUserT userInfo = getBscUserTByUserCode(userInfoDto.getId(), userInfoDto.getUserCode());
        if (Constants.UNACTIVAT_STATUE.equals(userInfo.getActivateStatus())) {
            throw new CheckException(new StringBuilder("用户").append(userInfo.getUserCode()).append("未激活，不允许修改！").toString());
        }
        //copy数据
        BscUserT bscUserT = new BscUserT();
        //bscUserT.setId(userInfoDto.getId());
        bscUserT.setRemark(userInfoDto.getRemark());
        bscUserT.setUserName(userInfoDto.getUserName());

        //修改数据
        try {
            QueryWrapper<BscUserT> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_code", userInfoDto.getUserCode());
            bscUserTMapper.update(bscUserT, queryWrapper);
        } catch (Exception e) {
            log.error("修改用户失败", e);
            throw new CheckException("修改用户失败，请重试！");
        }

    }

    /**
     * 根号userCode或ID获取用户信息
     *
     * @param id
     * @param userCode
     * @return
     */
    private BscUserT getBscUserTByUserCode(Long id, String userCode) {
        QueryWrapper<BscUserT> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotEmpty(userCode), "user_code", userCode);
        queryWrapper.eq("data_status", Constants.DATA_STATUE_OK);
        List<BscUserT> userTList = bscUserTMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userTList)) {
            throw new CheckException(new StringBuilder("未查询到用户").append(userCode).append("信息，请重试！").toString());
        }
        return userTList.get(0);
    }


    @Transactional
    @Override
    public void updateSafeData(UserInfoDto userInfoDto) {
        //TODO 待处理 修改前的验证

        //校验数据
        CommonUtils.checkLongException(userInfoDto.getId(), "请选择要修改的用户");

        //数据状态判断
        BscUserT userInfo = getBscUserTByUserCode(userInfoDto.getId(), userInfoDto.getUserCode());
        if (Constants.UNACTIVAT_STATUE.equals(userInfo.getActivateStatus())) {
            throw new CheckException(new StringBuilder("用户").append(userInfo.getUserCode()).append("未激活，不允许修改！").toString());
        }

        //copy数据
        BscUserT bscUserT = new BscUserT();
        bscUserT.setId(userInfoDto.getId());
        bscUserT.setUserPwd(encryptionUserPwd(userInfoDto.getUserPwd()));
        bscUserT.setLoginEmail(userInfoDto.getLoginEmail());
        bscUserT.setLoginPhone(userInfoDto.getLoginPhone());
//        if (StringUtils.isNotEmpty(userInfoDto.getLoginEmail())||StringUtils.isNotEmpty(userInfoDto.getLoginPhone())){
//            bscUserT.set(userInfoDto.getLoginPhone());
//        }
        //修改数据
        try {
            bscUserTMapper.updateById(bscUserT);
        } catch (Exception e) {
            throw new CheckException("修改用户失败，请重试！");
        }
        //发送验证码


    }

    /**
     * 密码加密
     *
     * @param userPwd
     */
    private String encryptionUserPwd(String userPwd) {
        if (StringUtils.isEmpty(userPwd)) {
            return "system";
        }
        return userPwd;
    }

}
