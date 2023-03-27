package com.floating.hikaru.module.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.floating.hikaru.framework.common.enums.CommonStatusEnum;
import com.floating.hikaru.framework.common.util.collection.CollectionUtils;
import com.floating.hikaru.framework.common.util.string.StrUtils;
import com.floating.hikaru.module.system.ErrorCodeConstants;
import com.floating.hikaru.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.floating.hikaru.module.system.dal.mysql.oauth2.OAuth2ClientMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.floating.hikaru.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * OAuth2.0 Client Service 实现类
 */
@Service
@Validated
@Slf4j
public class OAuth2ClientServiceImpl implements OAuth2ClientService {

    /**
     * 客户端缓存
     * key：客户端编号 {@link OAuth2ClientDO#getClientId()} ()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter // 解决单测
    @Setter // 解决单测
    private volatile Map<String, OAuth2ClientDO> clientCache;

    @Resource
    private OAuth2ClientMapper oauth2ClientMapper;

    /**
     * 初始化 {@link #clientCache} 缓存
     */
    @Override
    @PostConstruct
    public void initLocalCache() {
        // 第一步：查询数据
        List<OAuth2ClientDO> clients = oauth2ClientMapper.selectList();
        log.info("[initLocalCache][缓存 OAuth2 客户端，数量为:{}]", clients.size());

        // 第二步：构建缓存。
        clientCache = CollectionUtils.convertMap(clients, OAuth2ClientDO::getClientId);
    }

    @Override
    public OAuth2ClientDO validOAuthClientFromCache(String clientId, String clientSecret,
                                                    String authorizedGrantType, Collection<String> scopes, String redirectUri) {
        // 校验客户端存在、且开启
        OAuth2ClientDO client = clientCache.get(clientId);
        if (client == null) {
            throw exception(ErrorCodeConstants.OAUTH2_CLIENT_NOT_EXISTS);
        }
        if (ObjectUtil.notEqual(client.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            throw exception(ErrorCodeConstants.OAUTH2_CLIENT_DISABLE);
        }

        // 校验客户端密钥
        if (StrUtil.isNotEmpty(clientSecret) && ObjectUtil.notEqual(client.getSecret(), clientSecret)) {
            throw exception(ErrorCodeConstants.OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
        }
        // 校验授权方式
        if (StrUtil.isNotEmpty(authorizedGrantType) && !CollUtil.contains(client.getAuthorizedGrantTypes(), authorizedGrantType)) {
            throw exception(ErrorCodeConstants.OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
        }
        // 校验授权范围
        if (CollUtil.isNotEmpty(scopes) && !CollUtil.containsAll(client.getScopes(), scopes)) {
            throw exception(ErrorCodeConstants.OAUTH2_CLIENT_SCOPE_OVER);
        }
        // 校验回调地址
        if (StrUtil.isNotEmpty(redirectUri) && !StrUtils.startWithAny(redirectUri, client.getRedirectUris())) {
            throw exception(ErrorCodeConstants.OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, redirectUri);
        }
        return client;
    }

}
