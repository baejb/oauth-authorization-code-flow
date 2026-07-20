package com.aistudy.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 기본 스프링부트 애플리케이션 진입점.
 *
 * <p>이 커밋은 "돌아가는 기본 스프링부트"만 담는다. OAuth 2.0 인가 코드 흐름은 이후 별도 브랜치에서
 * 추가된다({@code docs/oauth_authorization_code_flow.svg} 참고).
 */
@SpringBootApplication
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }
}
