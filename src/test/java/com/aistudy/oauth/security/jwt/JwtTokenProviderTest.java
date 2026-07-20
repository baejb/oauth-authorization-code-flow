package com.aistudy.oauth.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.aistudy.oauth.security.principal.UserPrincipal;
import com.aistudy.oauth.user.AuthProvider;
import com.aistudy.oauth.user.User;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-secret-key-that-is-at-least-32-bytes-long!!", 3_600_000L);
        tokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    @DisplayName("발급한 토큰은 검증에 통과하고, 주체/권한을 그대로 복원한다")
    void createAndAuthenticateRoundTrip() {
        UserPrincipal principal = principalWithId(42L);

        String token = tokenProvider.createToken(principal);

        assertThat(tokenProvider.validate(token)).isTrue();
        Authentication authentication = tokenProvider.getAuthentication(token);
        assertThat(authentication.getPrincipal()).isEqualTo(42L);
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("서명이 맞지 않는 토큰은 검증에 실패한다")
    void rejectsTamperedToken() {
        assertThat(tokenProvider.validate("not-a-valid-token")).isFalse();
    }

    private UserPrincipal principalWithId(long id) {
        User user = User.of(AuthProvider.GOOGLE, "sub-" + id, "u@test.com", "tester", null);
        ReflectionTestUtils.setField(user, "id", id); // JPA 가 채우는 id 를 테스트에서 주입
        return UserPrincipal.of(user, Map.of("sub", "sub-" + id));
    }
}
