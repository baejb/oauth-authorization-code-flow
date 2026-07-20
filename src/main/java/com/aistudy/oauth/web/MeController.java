package com.aistudy.oauth.web;

import com.aistudy.oauth.security.principal.UserPrincipal;
import com.aistudy.oauth.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 현재 로그인한 사용자 정보를 반환하는 API.
 *
 * <p>flow ⑧ 이후 발급된 우리 서비스 JWT 를 {@code Authorization: Bearer} 로 보내면
 * {@link com.aistudy.oauth.security.jwt.JwtAuthenticationFilter} 가 인증을 채운다.
 */
@RestController
@RequestMapping("/api")
public class MeController {

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return UserResponse.from(userService.getById(userId));
    }

    /**
     * 인증 주체에서 사용자 식별자를 꺼낸다. JWT 인증(필터)이면 principal 은 userId(Long)이고,
     * 세션 기반 OAuth2 로그인 직후라면 {@link UserPrincipal} 이다. 두 경로를 모두 지원한다.
     */
    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }
        if (principal instanceof Long userId) {
            return userId;
        }
        throw new IllegalStateException("지원하지 않는 인증 주체 타입입니다: " + principal.getClass());
    }
}
