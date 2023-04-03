package com.floating.hikaru.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import com.floating.hikaru.module.system.dal.dataobject.user.AdminUserDO;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

/**
 * 后台用户 Service 接口
 */
public interface AdminUserService {

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    AdminUserDO getUserByUsername(String username);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    String createUser(String str);
}
