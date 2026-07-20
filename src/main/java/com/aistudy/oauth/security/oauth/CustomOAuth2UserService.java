package com.aistudy.oauth.security.oauth;

import com.aistudy.oauth.security.oauth.userinfo.OAuth2UserInfo;
import com.aistudy.oauth.security.oauth.userinfo.OAuth2UserInfoFactory;
import com.aistudy.oauth.security.principal.UserPrincipal;
import com.aistudy.oauth.user.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * 일반 OAuth2 제공자(카카오)용 사용자 서비스.
 *
 * <p><b>상속</b>: {@link DefaultOAuth2UserService} 를 확장해 프레임워크의 user-info 조회(flow ⑦)를
 * 그대로 재사용하고, 그 결과에 "우리 서비스 사용자 등록 + principal 변환"만 덧붙인다.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserInfoFactory userInfoFactory;
    private final OAuth2AccountService accountService;

    public CustomOAuth2UserService(OAuth2UserInfoFactory userInfoFactory, OAuth2AccountService accountService) {
        this.userInfoFactory = userInfoFactory;
        this.accountService = accountService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest); // ⑦ user-info 엔드포인트 조회
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo = userInfoFactory.create(registrationId, oauth2User.getAttributes());
        User user = accountService.register(registrationId, userInfo); // ⑧ DB 조회/가입

        return UserPrincipal.of(user, oauth2User.getAttributes());
    }
}
