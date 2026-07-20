# OAuth 2.0 Authorization Code Flow (Spring Boot)

Spring Security OAuth2 Client 를 사용해 **인가 코드 흐름(Authorization Code Flow)** 을 구글/카카오
소셜 로그인으로 구현한 학습용 프로젝트입니다. 인가 코드 흐름의 각 단계를 코드에 대응시켰고,
객체지향 4대 원칙과 SOLID 원칙을 지키는 데 초점을 두었습니다.

## 기술 스택

- Java 25, Spring Boot 4.0.x
- Spring Security OAuth2 Client (Authorization Code + PKCE + state 자동 처리)
- Spring Data JPA + H2 (인메모리, 학습용)
- JJWT (우리 서비스 자체 JWT 발급/검증)
- Gradle (Wrapper 포함)

## 흐름 단계별 대응

| 인가 코드 흐름 단계 | 담당 |
| --- | --- |
| ① 로그인 버튼 클릭 | `static/index.html` → `/oauth2/authorization/{google\|kakao}` |
| ② IdP 리다이렉트 (state, PKCE) | Spring Security `oauth2Login` 자동 처리 |
| ③ IdP 로그인 & 동의 | IdP(구글/카카오) |
| ④ code 콜백 | `/login/oauth2/code/{registrationId}` (프레임워크 기본) |
| ⑤ code 전달 | 프레임워크 → 커스텀 UserService |
| ⑥ 토큰 교환 (code + secret + verifier) | 프레임워크 백채널 |
| ⑦ access_token / id_token 발급·검증 | `CustomOidcUserService` / `CustomOAuth2UserService` |
| ⑧ DB 조회·가입 + 우리 서비스 JWT 발급 | `OAuth2AccountService` + `UserService` + `OAuth2AuthenticationSuccessHandler` |

프론트 채널(①~⑤)과 백 채널(⑥~⑦)의 구분은 프레임워크가 담당하고, 우리 코드는 ⑦의 후처리와 ⑧에
집중합니다.

## 패키지 구조

```
com.aistudy.oauth
├── config
│   ├── SecurityConfig               # 보안 조립 지점 (DIP: 협력자 주입·조립)
│   └── AppOAuth2Properties          # 로그인 후 리다이렉트 주소 설정
├── user                             # 도메인 (security 에 의존하지 않음)
│   ├── User                         # 캡슐화: 정적 팩토리 + 행위 메서드, setter 없음
│   ├── AuthProvider / Role
│   ├── UserRepository               # DIP: 영속성 추상화
│   └── UserService                  # SRP: 사용자 조회/가입 유스케이스
├── security
│   ├── oauth
│   │   ├── userinfo
│   │   │   ├── OAuth2UserInfo        # 추상화: 제공자 공통 사용자 정보
│   │   │   ├── GoogleOAuth2UserInfo  # 다형성 (구글 매핑)
│   │   │   ├── KakaoOAuth2UserInfo   # 다형성 (카카오 매핑)
│   │   │   └── OAuth2UserInfoFactory # OCP: 제공자 추가 시 등록만
│   │   ├── OAuth2AccountService      # DRY/SRP: 등록 규칙 일원화
│   │   ├── CustomOAuth2UserService   # 상속: DefaultOAuth2UserService 확장 (카카오)
│   │   └── CustomOidcUserService     # 상속: OidcUserService 확장 (구글)
│   ├── principal/UserPrincipal       # 상속·다형성: OidcUser 어댑터
│   ├── jwt
│   │   ├── TokenProvider             # ISP/DIP: 토큰 추상화
│   │   ├── JwtTokenProvider          # 구현
│   │   ├── JwtAuthenticationFilter   # Bearer 토큰 인증
│   │   └── JwtProperties
│   └── handler
│       ├── OAuth2AuthenticationSuccessHandler  # ⑧ JWT 발급 후 리다이렉트
│       └── OAuth2AuthenticationFailureHandler
└── web
    ├── MeController                  # GET /api/me (발급된 JWT 로 인증)
    └── UserResponse
```

## 원칙 적용 요약

- **SRP**: 토큰 발급(`JwtTokenProvider`), 사용자 등록(`OAuth2AccountService`/`UserService`),
  속성 매핑(`OAuth2UserInfo`), 흐름 조율(핸들러)이 각각 분리됨.
- **OCP**: 새 소셜 제공자는 `AuthProvider` 상수 + `OAuth2UserInfo` 구현 + 팩토리 등록만으로 추가.
  상위 코드(`SecurityConfig`, 커스텀 UserService)는 수정 불필요.
- **LSP**: `GoogleOAuth2UserInfo`/`KakaoOAuth2UserInfo` 는 `OAuth2UserInfo` 자리에 완전 대체 가능.
- **ISP**: `OAuth2UserInfo`, `TokenProvider` 는 소비자가 실제 쓰는 최소 메서드만 노출.
- **DIP**: `SecurityConfig`·핸들러·필터가 구현이 아닌 추상화(`TokenProvider`, `UserRepository`)에 의존.
- **캡슐화/상속/다형성/추상화**: 각각 `User`, `Custom*UserService`/`UserPrincipal`,
  `OAuth2UserInfo` 구현군, 인터페이스들로 구현.

## 실행 방법

1. IdP 클라이언트 등록
   - Google Cloud Console에서 OAuth 클라이언트 생성, 승인된 리디렉션 URI:
     `http://localhost:8080/login/oauth2/code/google`
   - Kakao Developers에서 앱 생성, Redirect URI:
     `http://localhost:8080/login/oauth2/code/kakao`
2. 환경변수 설정 (**실제 값은 절대 커밋하지 마세요**)
   ```bash
   export GOOGLE_CLIENT_ID=...      export GOOGLE_CLIENT_SECRET=...
   export KAKAO_CLIENT_ID=...       export KAKAO_CLIENT_SECRET=...
   export JWT_SECRET=$(openssl rand -base64 48)
   ```
3. 실행
   ```bash
   ./gradlew bootRun        # Windows: gradlew.bat bootRun
   ```
4. 브라우저에서 `http://localhost:8080` → 로그인 → 발급된 JWT 로 `/api/me` 호출 확인

## 테스트

```bash
./gradlew test
```

- 도메인 불변식(`UserTest`), 제공자 매핑/OCP(`OAuth2UserInfoFactoryTest`),
  JWT 왕복(`JwtTokenProviderTest`), 컨텍스트 로드(`OauthApplicationTests`).

## 보안 주의

- `client-secret`, `JWT_SECRET` 등 비밀 값은 `.gitignore` 로 보호되며 환경변수로만 주입합니다.
- H2 콘솔과 인메모리 DB는 **학습용**입니다. 운영에서는 실제 DB와 적절한 세션/토큰 전략으로 교체하세요.
