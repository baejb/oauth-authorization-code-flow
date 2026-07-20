package com.aistudy.oauth.user;

/**
 * 우리 서비스 내부 권한. Spring Security 권한 문자열은 {@code ROLE_} 접두사 규칙을 따른다.
 */
public enum Role {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String authority() {
        return authority;
    }
}
