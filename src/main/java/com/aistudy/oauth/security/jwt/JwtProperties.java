package com.aistudy.oauth.security.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 우리 서비스 JWT 설정. {@code app.jwt.*} 로 외부화한다.
 *
 * <p><b>캡슐화</b>: 비밀키/만료시간을 코드 상수가 아닌 설정으로 분리하고 불변 record 로 노출한다.
 * 시작 시점에 유효성(비어있지 않음, 양수)을 검증한다.
 *
 * @param secret       HMAC-SHA 서명 키. HS256 안전성을 위해 최소 32바이트 이상을 권장한다.
 * @param expirationMs 액세스 토큰 유효기간(밀리초)
 */
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @Positive long expirationMs) {
}
