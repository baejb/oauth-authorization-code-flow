package com.aistudy.oauth.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;

/**
 * 우리 서비스의 사용자.
 *
 * <p><b>캡슐화</b>: 모든 필드는 private 이며 setter 를 제공하지 않는다. 생성은 정적 팩토리
 * {@link #of(AuthProvider, String, String, String)} 로만 가능하고, 상태 변경은 의도가 드러나는
 * 행위 메서드({@link #updateProfile(String, String)})로만 허용해 불변식(제공자+제공자ID 조합의
 * 고유성 등)을 객체 스스로 지킨다.
 *
 * <p>한 사용자는 (제공자, 제공자ID) 조합으로 유일하게 식별된다. 같은 이메일이라도 구글 계정과
 * 카카오 계정은 서로 다른 사용자로 취급한다.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_provider",
                columnNames = {"provider", "provider_id"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false, updatable = false)
    private String providerId;

    @Column
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** JPA 전용 기본 생성자. 외부에서 직접 호출하지 못하도록 protected 로 둔다. */
    protected User() {
    }

    private User(AuthProvider provider, String providerId, String email, String name, String imageUrl, Role role) {
        this.provider = Objects.requireNonNull(provider, "provider 는 필수입니다");
        this.providerId = requireText(providerId, "providerId 는 필수입니다");
        this.name = requireText(name, "name 은 필수입니다");
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = Objects.requireNonNull(role, "role 은 필수입니다");
    }

    /** 신규 사용자를 기본 권한({@link Role#USER})으로 생성한다. */
    public static User of(AuthProvider provider, String providerId, String email, String name, String imageUrl) {
        return new User(provider, providerId, email, name, imageUrl, Role.USER);
    }

    /**
     * 재로그인 시 IdP 로부터 받은 최신 프로필로 갱신한다.
     * 제공자/제공자ID/권한 등 식별·보안에 관계된 값은 이 메서드로 바뀌지 않는다.
     *
     * @return 메서드 체이닝을 위한 자기 자신
     */
    public User updateProfile(String name, String imageUrl) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.imageUrl = imageUrl;
        return this;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public Long getId() {
        return id;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Role getRole() {
        return role;
    }
}
