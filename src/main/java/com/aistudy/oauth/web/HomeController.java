package com.aistudy.oauth.web;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 애플리케이션 기동 여부를 확인하는 기본 엔드포인트.
 * 인증 없이 접근 가능하며, 스프링부트가 정상 구동되는지 확인하는 용도다.
 */
@RestController
public class HomeController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "app", "oauth");
    }
}
