# OAuth 2.0 Study (Spring Boot)

OAuth 2.0 인가 코드 흐름(Authorization Code Flow)을 Spring Boot 로 학습하기 위한 프로젝트입니다.

이 브랜치(`main`)에는 **동작하는 기본 스프링부트 골격**만 들어 있습니다. 소셜 로그인(구글/카카오)
OAuth 구현은 별도 브랜치에서 PR 로 추가됩니다.

## 기술 스택

- Java 25, Spring Boot 4.0.x
- Gradle (Wrapper 포함)

## 실행

```bash
./gradlew bootRun        # Windows: gradlew.bat bootRun
```

- 헬스 체크: `GET http://localhost:8080/api/health` → `{"status":"UP","app":"oauth"}`

## 테스트

```bash
./gradlew test
```

## 참고

- 구현 목표 흐름은 `docs/oauth_authorization_code_flow.svg` 를 참고하세요.
