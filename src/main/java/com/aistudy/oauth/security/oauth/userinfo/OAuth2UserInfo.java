package com.aistudy.oauth.security.oauth.userinfo;

/**
 * 제공자마다 제각각인 사용자 속성(attributes) 구조를 우리 서비스가 필요로 하는 공통 형태로
 * 노출하는 추상화.
 *
 * <p><b>추상화·다형성</b>: 구글은 {@code sub}, 카카오는 {@code id} 처럼 같은 의미의 값이 서로 다른
 * 키에 담겨 온다. 상위 로직(CustomOAuth2UserService 등)은 이 인터페이스에만 의존하므로 제공자별
 * 차이를 알 필요가 없다.
 *
 * <p><b>ISP</b>: 우리 서비스가 실제로 쓰는 최소한의 정보만 노출한다.
 */
public interface OAuth2UserInfo {

    /** 제공자 측 고유 식별자(구글 sub, 카카오 id). 절대 null 이 아니다. */
    String getProviderId();

    /** 이메일. 제공자 정책/동의 범위에 따라 null 일 수 있다. */
    String getEmail();

    /** 표시 이름. 없으면 이메일 등에서 유도하거나 기본값을 채운다. */
    String getName();

    /** 프로필 이미지 URL. 없으면 null. */
    String getImageUrl();
}
