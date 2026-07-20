package com.aistudy.oauth.security.oauth;

import com.aistudy.oauth.security.oauth.userinfo.OAuth2UserInfo;
import com.aistudy.oauth.security.oauth.userinfo.OAuth2UserInfoFactory;
import com.aistudy.oauth.security.principal.UserPrincipal;
import com.aistudy.oauth.user.User;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * OIDC 제공자(구글)용 사용자 서비스.
 *
 * <p><b>상속</b>: {@link OidcUserService} 를 확장한다. {@code super.loadUser} 가 id_token 검증(flow ⑦)과
 * user-info 조회를 수행하므로, 우리는 그 결과에 사용자 등록(⑧)과 principal 변환만 더한다.
 *
 * <p>{@link CustomOAuth2UserService} 와 동일한 협력자({@link OAuth2UserInfoFactory},
 * {@link OAuth2AccountService})를 재사용하므로 등록 규칙이 두 경로에서 일관된다.
 */
@Service
public class CustomOidcUserService extends OidcUserService {

    private final OAuth2UserInfoFactory userInfoFactory;
    private final OAuth2AccountService accountService;

    public CustomOidcUserService(OAuth2UserInfoFactory userInfoFactory, OAuth2AccountService accountService) {
        this.userInfoFactory = userInfoFactory;
        this.accountService = accountService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest); // ⑦ id_token 검증 + user-info 조회
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo = userInfoFactory.create(registrationId, oidcUser.getAttributes());
        User user = accountService.register(registrationId, userInfo); // ⑧ DB 조회/가입

        return UserPrincipal.ofOidc(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
