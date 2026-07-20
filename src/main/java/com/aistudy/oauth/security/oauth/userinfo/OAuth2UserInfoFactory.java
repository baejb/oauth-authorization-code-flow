package com.aistudy.oauth.security.oauth.userinfo;

import com.aistudy.oauth.user.AuthProvider;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * registrationId 에 맞는 {@link OAuth2UserInfo} 구현을 생성하는 팩토리.
 *
 * <p><b>OCP</b>: 새 제공자를 지원하려면 (1) {@link AuthProvider} 에 상수를 추가하고
 * (2) {@link OAuth2UserInfo} 구현 클래스를 만든 뒤 (3) 아래 registry 에 한 줄 등록하면 된다.
 * 이 팩토리를 사용하는 상위 코드(CustomOAuth2UserService 등)는 전혀 수정할 필요가 없다.
 */
@Component
public class OAuth2UserInfoFactory {

    private final Map<AuthProvider, Function<Map<String, Object>, OAuth2UserInfo>> registry =
            new EnumMap<>(AuthProvider.class);

    public OAuth2UserInfoFactory() {
        registry.put(AuthProvider.GOOGLE, GoogleOAuth2UserInfo::new);
        registry.put(AuthProvider.KAKAO, KakaoOAuth2UserInfo::new);
    }

    /**
     * @param registrationId Spring Security ClientRegistration id (예: "google", "kakao")
     * @param attributes     IdP 가 내려준 원본 사용자 속성
     */
    public OAuth2UserInfo create(String registrationId, Map<String, Object> attributes) {
        AuthProvider provider = AuthProvider.from(registrationId);
        Function<Map<String, Object>, OAuth2UserInfo> mapper = registry.get(provider);
        if (mapper == null) {
            throw new IllegalStateException("등록되지 않은 제공자입니다: " + provider);
        }
        return mapper.apply(attributes);
    }
}
