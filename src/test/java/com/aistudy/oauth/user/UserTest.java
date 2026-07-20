package com.aistudy.oauth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("신규 사용자는 기본 권한 USER 로 생성된다")
    void createsWithDefaultRole() {
        User user = User.of(AuthProvider.GOOGLE, "sub-1", "a@b.com", "홍길동", null);

        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(user.getProviderId()).isEqualTo("sub-1");
    }

    @Test
    @DisplayName("필수 값이 없으면 생성 시점에 거부한다(불변식 보호)")
    void rejectsBlankRequiredFields() {
        assertThatThrownBy(() -> User.of(AuthProvider.KAKAO, " ", null, "이름", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> User.of(AuthProvider.KAKAO, "id", null, "  ", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("프로필 갱신은 이름/이미지만 바꾸고, 빈 이름은 무시한다")
    void updatesProfileOnly() {
        User user = User.of(AuthProvider.KAKAO, "id-1", "a@b.com", "old", "old.png");

        user.updateProfile("new", "new.png");
        assertThat(user.getName()).isEqualTo("new");
        assertThat(user.getImageUrl()).isEqualTo("new.png");

        user.updateProfile("  ", null);
        assertThat(user.getName()).isEqualTo("new"); // 빈 이름은 무시
        assertThat(user.getImageUrl()).isNull();
    }
}
