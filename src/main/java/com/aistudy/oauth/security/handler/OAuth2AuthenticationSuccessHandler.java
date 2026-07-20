package com.aistudy.oauth.security.handler;

import com.aistudy.oauth.config.AppOAuth2Properties;
import com.aistudy.oauth.security.jwt.TokenProvider;
import com.aistudy.oauth.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 소셜 로그인 성공 시 우리 서비스 JWT 를 발급하고, 설정된 프론트 주소로 토큰과 함께 리다이렉트한다
 * (flow ⑧ "우리 서비스 세션/JWT 발급").
 *
 * <p><b>SRP·DIP</b>: 토큰 발급 방법은 {@link TokenProvider} 에, 사용자 정보 구성은 UserService 경로에
 * 위임하고, 이 핸들러는 "성공 후 무엇을 할지(토큰 발급→리다이렉트)"라는 흐름만 조율한다.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AppOAuth2Properties properties;

    public OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppOAuth2Properties properties) {
        this.tokenProvider = tokenProvider;
        this.properties = properties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.createToken(principal);

        String targetUrl = UriComponentsBuilder.fromUriString(properties.authorizedRedirectUri())
                .queryParam("token", token)
                .build().toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
