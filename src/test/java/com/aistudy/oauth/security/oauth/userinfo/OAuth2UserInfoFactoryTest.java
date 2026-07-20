package com.aistudy.oauth.security.oauth.userinfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuth2UserInfoFactoryTest {

    private final OAuth2UserInfoFactory factory = new OAuth2UserInfoFactory();

    @Test
    @DisplayName("구글 속성을 표준 형태로 매핑한다")
    void mapsGoogleAttributes() {
        OAuth2UserInfo info = factory.create("google", Map.of(
                "sub", "1234567890",
                "email", "user@gmail.com",
                "name", "홍길동",
                "picture", "https://img/google.png"));

        assertThat(info).isInstanceOf(GoogleOAuth2UserInfo.class);
        assertThat(info.getProviderId()).isEqualTo("1234567890");
        assertThat(info.getEmail()).isEqualTo("user@gmail.com");
        assertThat(info.getName()).isEqualTo("홍길동");
        assertThat(info.getImageUrl()).isEqualTo("https://img/google.png");
    }

    @Test
    @DisplayName("카카오의 중첩 속성을 표준 형태로 매핑한다")
    void mapsKakaoNestedAttributes() {
        OAuth2UserInfo info = factory.create("kakao", Map.of(
                "id", 9876543210L,
                "properties", Map.of("nickname", "카카오유저", "profile_image", "https://img/kakao.png"),
                "kakao_account", Map.of("email", "user@kakao.com")));

        assertThat(info).isInstanceOf(KakaoOAuth2UserInfo.class);
        assertThat(info.getProviderId()).isEqualTo("9876543210");
        assertThat(info.getEmail()).isEqualTo("user@kakao.com");
        assertThat(info.getName()).isEqualTo("카카오유저");
        assertThat(info.getImageUrl()).isEqualTo("https://img/kakao.png");
    }

    @Test
    @DisplayName("지원하지 않는 제공자는 예외를 던진다")
    void rejectsUnknownProvider() {
        assertThatThrownBy(() -> factory.create("naver", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
