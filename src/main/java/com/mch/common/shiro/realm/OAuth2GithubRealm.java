package com.mch.common.shiro.realm;

import com.mch.common.constants.AuthcTypeEnum;
import org.springframework.stereotype.Component;

/**
 * Github OAuth2 Realm
 */
@Component
public class OAuth2GithubRealm extends OAuth2Realm {

    @Override
    public AuthcTypeEnum getAuthcTypeEnum() {
        return AuthcTypeEnum.GITHUB;
    }
}