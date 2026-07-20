package com.aistudy.oauth.user;

import java.util.Arrays;

/**
 * 지원하는 소셜 로그인 제공자(IdP).
 *
 * <p>{@link #registrationId} 는 Spring Security 의 {@code ClientRegistration} id 와 1:1로 대응한다.
 * 새 제공자를 추가할 때는 이 enum 에 상수 하나를 더하고 대응하는
 * {@link com.aistudy.oauth.security.oauth.userinfo.OAuth2UserInfo} 구현을 등록하면 되므로,
 * 기존 코드를 수정하지 않는다(OCP).
 */
public enum AuthProvider {

    GOOGLE("google"),
    KAKAO("kakao");

    private final String registrationId;

    AuthProvider(String registrationId) {
        this.registrationId = registrationId;
    }

    public String registrationId() {
        return registrationId;
    }

    /**
     * Spring Security 의 registrationId 로부터 대응하는 제공자를 찾는다.
     *
     * @throws IllegalArgumentException 지원하지 않는 제공자인 경우
     */
    public static AuthProvider from(String registrationId) {
        return Arrays.stream(values())
                .filter(provider -> provider.registrationId.equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "지원하지 않는 OAuth2 제공자입니다: " + registrationId));
    }
}
