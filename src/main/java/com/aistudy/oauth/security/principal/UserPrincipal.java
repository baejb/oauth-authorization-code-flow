package com.aistudy.oauth.security.principal;

import com.aistudy.oauth.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * Spring Security 인증 주체(principal)이면서 우리 서비스 사용자 식별자를 함께 들고 다니는 어댑터.
 *
 * <p><b>상속·다형성</b>: {@link OidcUser}(= {@link org.springframework.security.oauth2.core.user.OAuth2User}
 * 의 하위 타입)를 구현하므로, OIDC 제공자(구글)와 일반 OAuth2 제공자(카카오) 양쪽 경로에서 동일한
 * 타입 하나로 principal 을 반환할 수 있다. 카카오처럼 OIDC 가 아닌 경우 {@link #getIdToken()} 등
 * OIDC 전용 값은 null 이다.
 *
 * <p>인스턴스 생성은 두 정적 팩토리로만 허용해 각 경로의 의도를 명확히 한다.
 */
public final class UserPrincipal implements OidcUser {

    private final Long userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;   // 카카오(비 OIDC)는 null
    private final OidcUserInfo userInfo; // 카카오(비 OIDC)는 null

    private UserPrincipal(Long userId,
                          String email,
                          Collection<? extends GrantedAuthority> authorities,
                          Map<String, Object> attributes,
                          OidcIdToken idToken,
                          OidcUserInfo userInfo) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
        this.attributes = attributes;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    /** 일반 OAuth2(카카오) 경로용. */
    public static UserPrincipal of(User user, Map<String, Object> attributes) {
        return new UserPrincipal(user.getId(), user.getEmail(), authoritiesOf(user), attributes, null, null);
    }

    /** OIDC(구글) 경로용. id_token / userInfo 를 함께 보관한다. */
    public static UserPrincipal ofOidc(User user,
                                       Map<String, Object> attributes,
                                       OidcIdToken idToken,
                                       OidcUserInfo userInfo) {
        return new UserPrincipal(user.getId(), user.getEmail(), authoritiesOf(user), attributes, idToken, userInfo);
    }

    private static List<GrantedAuthority> authoritiesOf(User user) {
        return List.of(new SimpleGrantedAuthority(user.getRole().authority()));
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    // --- OAuth2User ---

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** principal 의 name 은 우리 서비스의 사용자 식별자(userId)로 통일한다. */
    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    // --- OidcUser (구글 경로에서만 유효, 카카오는 null 반환) ---

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
}
