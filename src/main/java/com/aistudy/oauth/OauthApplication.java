package com.aistudy.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * OAuth 2.0 Authorization Code Flow 학습용 애플리케이션 진입점.
 *
 * <p>인가 코드 흐름을 Spring Security OAuth2 Client 로 구현한다. 프론트 채널(①~⑤)과 백 채널(⑥~⑦)은
 * 프레임워크가 처리하고, 우리 서비스가 담당하는 사용자 조회/가입과 자체 JWT 발급(⑧)만 애플리케이션
 * 코드로 구현한다.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }
}
