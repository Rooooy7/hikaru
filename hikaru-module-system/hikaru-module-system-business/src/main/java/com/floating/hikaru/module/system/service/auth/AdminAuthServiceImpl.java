package com.floating.hikaru.module.system.service.auth;

import cn.hutool.core.util.ObjectUtil;
import com.floating.hikaru.framework.common.enums.CommonStatusEnum;
import com.floating.hikaru.framework.common.enums.UserTypeEnum;
import com.floating.hikaru.module.system.ErrorCodeConstants;
import com.floating.hikaru.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.floating.hikaru.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.floating.hikaru.module.system.convert.auth.AuthConvert;
import com.floating.hikaru.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.floating.hikaru.module.system.dal.dataobject.user.AdminUserDO;
import com.floating.hikaru.module.system.enums.oauths.OAuth2ClientConstants;
import com.floating.hikaru.module.system.service.oauth2.OAuth2TokenService;
import com.floating.hikaru.module.system.service.user.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.floating.hikaru.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private AdminUserService userService;

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Override
    public AdminUserDO authenticate(String username, String password) {
//        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);
        if (user == null) {
//            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
//            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (ObjectUtil.notEqual(user.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
//            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(ErrorCodeConstants.AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    public AuthLoginRespVO login(@Valid AuthLoginReqVO reqVO) {
        // 判断验证码是否正确
//        verifyCaptcha(reqVO);

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

        // 如果 socialType 非空，说明需要绑定社交用户
//        if (reqVO.getSocialType() != null) {
//            socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
//                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
//        }

        // 创建 Token 令牌，记录登录日志
//        createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(user.getId(), getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

//    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
//        // 插入登陆日志
//        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
//    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }

    @Override
    public void logout(String token, Integer logType) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(token);
        if (accessTokenDO == null) {
            return;
        }
        // 删除成功，则记录登出日志
//        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

//    private void createLogoutLog(Long userId, Integer userType, Integer logType) {
//        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
//        reqDTO.setLogType(logType);
//        reqDTO.setTraceId(TracerUtils.getTraceId());
//        reqDTO.setUserId(userId);
//        reqDTO.setUserType(userType);
//        if (ObjectUtil.equal(getUserType().getValue(), userType)) {
//            reqDTO.setUsername(getUsername(userId));
//        } else {
//            reqDTO.setUsername(memberService.getMemberUserMobile(userId));
//        }
//        reqDTO.setUserAgent(ServletUtils.getUserAgent());
//        reqDTO.setUserIp(ServletUtils.getClientIP());
//        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
//        loginLogService.createLoginLog(reqDTO);
//    }

//    private void createLoginLog(Long userId, String username,
//                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
//        // 插入登录日志
//        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
//        reqDTO.setLogType(logTypeEnum.getType());
//        reqDTO.setTraceId(TracerUtils.getTraceId());
//        reqDTO.setUserId(userId);
//        reqDTO.setUserType(getUserType().getValue());
//        reqDTO.setUsername(username);
//        reqDTO.setUserAgent(ServletUtils.getUserAgent());
//        reqDTO.setUserIp(ServletUtils.getClientIP());
//        reqDTO.setResult(loginResult.getResult());
//        loginLogService.createLoginLog(reqDTO);
//        // 更新最后登录时间
//        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
//            userService.updateUserLogin(userId, ServletUtils.getClientIP());
//        }
//    }

    @Override
    public AuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }
}
