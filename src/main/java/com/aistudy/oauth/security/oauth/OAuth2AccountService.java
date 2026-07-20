package com.aistudy.oauth.security.oauth;

import com.aistudy.oauth.security.oauth.userinfo.OAuth2UserInfo;
import com.aistudy.oauth.user.AuthProvider;
import com.aistudy.oauth.user.User;
import com.aistudy.oauth.user.UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;

/**
 * IdP 프로필({@link OAuth2UserInfo})을 우리 서비스 사용자로 변환·등록하는 얇은 조정 계층.
 *
 * <p><b>DRY·SRP</b>: OIDC(구글)와 일반 OAuth2(카카오) 두 커스텀 UserService 가 공통으로 필요로 하는
 * "검증 + 이름 보정 + upsert" 로직을 한곳에 모은다. 각 UserService 는 attributes 추출만 책임지고,
 * 사용자 등록 규칙은 이 클래스가 책임진다.
 */
@Service
public class OAuth2AccountService {

    private static final String MISSING_ID_ERROR = "invalid_provider_response";

    private final UserService userService;

    public OAuth2AccountService(UserService userService) {
        this.userService = userService;
    }

    public User register(String registrationId, OAuth2UserInfo info) {
        AuthProvider provider = AuthProvider.from(registrationId);

        String providerId = info.getProviderId();
        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(MISSING_ID_ERROR),
                    provider + " 응답에 사용자 식별자가 없습니다.");
        }

        String name = resolveName(info, provider, providerId);
        return userService.upsert(provider, providerId, info.getEmail(), name, info.getImageUrl());
    }

    /** 표시 이름이 없으면 이메일 로컬파트 → 제공자 식별자 순으로 대체한다. User 는 name 이 필수이기 때문. */
    private String resolveName(OAuth2UserInfo info, AuthProvider provider, String providerId) {
        if (info.getName() != null && !info.getName().isBlank()) {
            return info.getName();
        }
        String email = info.getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf('@'));
        }
        return provider.name().toLowerCase() + "_" + providerId;
    }
}
