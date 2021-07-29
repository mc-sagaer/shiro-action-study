package com.mch.common.shiro.realm;

import com.mch.common.constants.AuthcTypeEnum;
import org.springframework.stereotype.Component;

/**
 * Gitee OAuth2 Realm
 */
@Component
public class OAuth2GiteeRealm extends OAuth2Realm {

    @Override
    public AuthcTypeEnum getAuthcTypeEnum() {
        return AuthcTypeEnum.GITEE;
    }
}