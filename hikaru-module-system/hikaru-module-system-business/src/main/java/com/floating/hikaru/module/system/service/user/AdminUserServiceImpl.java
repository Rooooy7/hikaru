package com.floating.hikaru.module.system.service.user;

import com.floating.hikaru.framework.common.util.date.DateUtils;
import com.floating.hikaru.module.system.dal.dataobject.user.AdminUserDO;
import com.floating.hikaru.module.system.dal.mysql.user.AdminUserMapper;
import com.floating.hikaru.module.system.enums.common.SexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;

/**
 * 后台用户 Service 实现类
 */
@Service("adminUserService")
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminUserDO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }


    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String createUser(String str) {
        AdminUserDO newUser = new AdminUserDO(new Random(str.hashCode()).nextLong(), str, "123456", "roy", "", 1L, null, "384443697@qq.com", "13912312312", SexEnum.MALE.getSex(), "", 1, "", DateUtils.of(new Date()));
//        newUser.setCreateTime(DateUtils.of(new Date()));
//        newUser.setUpdateTime(DateUtils.of(new Date()));
        userMapper.insert(newUser);
        return str;
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
