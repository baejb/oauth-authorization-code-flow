package com.aistudy.oauth.security.oauth.userinfo;

import java.util.Map;

/**
 * 카카오(OAuth2 user-info) 사용자 속성 매핑.
 *
 * <p>카카오 응답은 중첩 구조다:
 * <pre>
 * {
 *   "id": 123456789,
 *   "properties":     { "nickname": "...", "profile_image": "..." },
 *   "kakao_account":  { "email": "...", "profile": { "nickname": "...", "profile_image_url": "..." } }
 * }
 * </pre>
 * 이메일은 비즈니스 앱 승인/동의 범위에 따라 없을 수 있으므로 null 을 허용한다.
 */
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return asString(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return asString(nested("kakao_account").get("email"));
    }

    @Override
    public String getName() {
        Object nickname = nested("properties").get("nickname");
        if (nickname != null) {
            return nickname.toString();
        }
        return asString(nestedIn(nested("kakao_account"), "profile").get("nickname"));
    }

    @Override
    public String getImageUrl() {
        Object image = nested("properties").get("profile_image");
        if (image != null) {
            return image.toString();
        }
        return asString(nestedIn(nested("kakao_account"), "profile").get("profile_image_url"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> nested(String key) {
        Object value = attributes.get(key);
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> nestedIn(Map<String, Object> source, String key) {
        Object value = source.get(key);
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
