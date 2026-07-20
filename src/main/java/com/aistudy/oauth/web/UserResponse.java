package com.aistudy.oauth.web;

import com.aistudy.oauth.user.User;

/**
 * 사용자 조회 API 응답 DTO. 엔티티를 그대로 노출하지 않고 필요한 필드만 외부에 드러낸다(캡슐화).
 */
public record UserResponse(
        Long id,
        String provider,
        String email,
        String name,
        String imageUrl,
        String role) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getProvider().name(),
                user.getEmail(),
                user.getName(),
                user.getImageUrl(),
                user.getRole().name());
    }
}
