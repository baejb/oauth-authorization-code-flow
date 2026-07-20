package com.aistudy.oauth.security.handler;

import com.aistudy.oauth.config.AppOAuth2Properties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 소셜 로그인 실패 시 에러 사유를 담아 프론트 주소로 되돌려 보낸다.
 *
 * <p>실패 원인(예: 사용자 동의 거부, 잘못된 응답)을 서버 세션에 숨기지 않고 프론트가 처리할 수 있게
 * 쿼리 파라미터로 전달한다. {@link UriComponentsBuilder} 가 값 인코딩을 처리한다.
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AppOAuth2Properties properties;

    public OAuth2AuthenticationFailureHandler(AppOAuth2Properties properties) {
        this.properties = properties;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String reason = exception.getMessage() == null ? "authentication_failed" : exception.getMessage();
        String targetUrl = UriComponentsBuilder.fromUriString(properties.authorizedRedirectUri())
                .queryParam("error", reason)
                .encode()
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
