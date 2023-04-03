package com.floating.hikaru.module.system.dal.mysql.oauth2;

import com.floating.hikaru.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.floating.hikaru.framework.mybatis.core.mapper.BaseMapperX;
import com.floating.hikaru.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2RefreshTokenMapper extends BaseMapperX<OAuth2RefreshTokenDO> {

    default int deleteByRefreshToken(String refreshToken) {
        return delete(new LambdaQueryWrapperX<OAuth2RefreshTokenDO>()
                .eq(OAuth2RefreshTokenDO::getRefreshToken, refreshToken));
    }

    default OAuth2RefreshTokenDO selectByRefreshToken(String refreshToken) {
        return selectOne(OAuth2RefreshTokenDO::getRefreshToken, refreshToken);
    }

}
