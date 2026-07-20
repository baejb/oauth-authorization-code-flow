package com.aistudy.oauth.config;

import com.aistudy.oauth.security.handler.OAuth2AuthenticationFailureHandler;
import com.aistudy.oauth.security.handler.OAuth2AuthenticationSuccessHandler;
import com.aistudy.oauth.security.jwt.JwtAuthenticationFilter;
import com.aistudy.oauth.security.jwt.TokenProvider;
import com.aistudy.oauth.security.oauth.CustomOAuth2UserService;
import com.aistudy.oauth.security.oauth.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 애플리케이션 보안 조립 지점.
 *
 * <p>인가 코드 흐름 단계와 코드의 대응:
 * <ul>
 *   <li>① 로그인 버튼 → {@code /oauth2/authorization/{google|kakao}} (프레임워크 기본 엔드포인트)</li>
 *   <li>②~③ IdP 리다이렉트 + state/PKCE 생성 → {@code oauth2Login} 이 자동 처리</li>
 *   <li>④~⑤ code 콜백 → {@code /login/oauth2/code/{registrationId}} (프레임워크 기본 콜백)</li>
 *   <li>⑥~⑦ 백채널 토큰 교환 + id_token 검증 → {@link CustomOidcUserService}/{@link CustomOAuth2UserService}</li>
 *   <li>⑧ DB 조회/가입 + 우리 서비스 JWT 발급 → 커스텀 UserService + {@link OAuth2AuthenticationSuccessHandler}</li>
 * </ul>
 *
 * <p><b>DIP·OCP</b>: 이 설정은 커스텀 협력자들을 "주입받아 조립"만 한다. 구현 세부(어떤 IdP 인지,
 * 토큰이 JWT 인지)를 알지 못하므로 제공자 추가·토큰 방식 교체 시 이 클래스는 거의 변하지 않는다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomOAuth2UserService oAuth2UserService,
            CustomOidcUserService oidcUserService,
            OAuth2AuthenticationSuccessHandler successHandler,
            OAuth2AuthenticationFailureHandler failureHandler,
            TokenProvider tokenProvider) throws Exception {

        http
                // JWT 기반 API 이므로 CSRF 비활성화, 폼/베이직 로그인 미사용
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html", "/loginSuccess.html", "/error",
                                "/favicon.ico", "/api/health")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)     // 카카오 등 일반 OAuth2
                                .oidcUserService(oidcUserService))  // 구글 등 OIDC
                        .successHandler(successHandler)
                        .failureHandler(failureHandler))
                // 우리 서비스 JWT 로 API 요청을 인증 (form 로그인 필터 앞에 배치)
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
