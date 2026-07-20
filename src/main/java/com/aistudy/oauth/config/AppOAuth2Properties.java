package com.aistudy.oauth.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 로그인 성공/실패 후 브라우저를 되돌려 보낼 프론트엔드 주소. {@code app.oauth2.*} 로 외부화한다.
 *
 * @param authorizedRedirectUri 로그인 완료 후 토큰(또는 에러)을 전달하며 리다이렉트할 URI
 */
@Validated
@ConfigurationProperties(prefix = "app.oauth2")
public record AppOAuth2Properties(
        @NotBlank String authorizedRedirectUri) {
}
