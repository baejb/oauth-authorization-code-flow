package com.aistudy.oauth.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 영속성 추상화.
 *
 * <p><b>DIP</b>: 애플리케이션 서비스({@link UserService})는 이 인터페이스에만 의존하고
 * 구현체(JPA)는 Spring Data 가 런타임에 주입한다. 저장 기술이 바뀌어도 상위 정책은 영향받지 않는다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
