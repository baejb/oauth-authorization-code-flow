package com.aistudy.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 전체 스프링 컨텍스트가 정상적으로 기동되는지 검증한다(빈 구성/설정 바인딩 스모크 테스트).
 * 더미 client-id/secret 로도 컨텍스트가 로드되어야 한다(시작 시 IdP 네트워크 호출 없음).
 */
@SpringBootTest
class OauthApplicationTests {

    @Test
    void contextLoads() {
    }
}
