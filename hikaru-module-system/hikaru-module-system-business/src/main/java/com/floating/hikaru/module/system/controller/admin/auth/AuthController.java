package com.floating.hikaru.module.system.controller.admin.auth;

import com.floating.hikaru.framework.common.pojo.CommonResult;
import com.floating.hikaru.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.floating.hikaru.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.floating.hikaru.module.system.service.auth.AdminAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static com.floating.hikaru.framework.common.pojo.CommonResult.success;

@Api(tags = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class AuthController {

    @Resource
    private AdminAuthService authService;

    @PostMapping("/login")
    @PermitAll
    @ApiOperation("使用账号密码登录")
//    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

    @GetMapping("/test")
    @PermitAll
    public CommonResult<String> test(@RequestParam String str) {
        return success(str);
    }
}
