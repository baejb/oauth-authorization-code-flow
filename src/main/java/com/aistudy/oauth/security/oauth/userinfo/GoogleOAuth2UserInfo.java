package com.aistudy.oauth.security.oauth.userinfo;

import java.util.Map;

/**
 * 구글(OIDC) 사용자 속성 매핑.
 *
 * <p>구글은 표준 OIDC 클레임을 사용한다: {@code sub}(고유 id), {@code email}, {@code name},
 * {@code picture}.
 */
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return asString(attributes.get("sub"));
    }

    @Override
    public String getEmail() {
        return asString(attributes.get("email"));
    }

    @Override
    public String getName() {
        return asString(attributes.get("name"));
    }

    @Override
    public String getImageUrl() {
        return asString(attributes.get("picture"));
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
