package com.aistudy.oauth.security.jwt;

import com.aistudy.oauth.security.principal.UserPrincipal;
import org.springframework.security.core.Authentication;

/**
 * 우리 서비스 토큰 발급/검증 추상화(flow ⑧).
 *
 * <p><b>DIP·ISP</b>: 성공 핸들러·인증 필터 등 상위 컴포넌트는 이 최소 인터페이스에만 의존한다.
 * 구현을 JWT 에서 다른 토큰 방식(예: opaque token + 저장소)으로 바꿔도 상위 코드는 그대로다.
 */
public interface TokenProvider {

    /** 인증된 사용자로부터 액세스 토큰을 발급한다. */
    String createToken(UserPrincipal principal);

    /** 토큰의 서명/만료를 검증한다. */
    boolean validate(String token);

    /** 토큰으로부터 {@link Authentication} 을 복원한다. */
    Authentication getAuthentication(String token);
}
