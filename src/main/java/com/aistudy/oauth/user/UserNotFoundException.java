package com.aistudy.oauth.user;

/**
 * 존재하지 않는 사용자를 조회했을 때 발생한다.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("사용자를 찾을 수 없습니다: id=" + id);
    }
}
