package com.aistudy.oauth.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 조회/가입 유스케이스(flow ⑧의 "DB 조회/가입 처리").
 *
 * <p><b>SRP</b>: 이 클래스의 유일한 책임은 "IdP 프로필로부터 우리 서비스 사용자를 확보"하는 것이다.
 * OAuth2 프로토콜 세부사항(토큰, id_token 검증 등)은 알지 못하며, 순수 도메인 값만 다룬다.
 * 그래서 security/oauth 패키지에 의존하지 않는다(의존성은 security → user 한 방향).
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 이미 있으면 프로필을 갱신하고, 없으면 새로 가입시킨다(upsert).
     *
     * @param provider   소셜 제공자
     * @param providerId 제공자 측 고유 사용자 식별자
     * @param email      이메일(제공자/동의 범위에 따라 null 일 수 있음)
     * @param name       표시 이름
     * @param imageUrl   프로필 이미지 URL(없으면 null)
     * @return 영속화된 사용자
     */
    @Transactional
    public User upsert(AuthProvider provider, String providerId, String email, String name, String imageUrl) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(existing -> existing.updateProfile(name, imageUrl))
                .orElseGet(() -> userRepository.save(User.of(provider, providerId, email, name, imageUrl)));
    }

    /**
     * 식별자로 사용자를 조회한다.
     *
     * @throws UserNotFoundException 해당 사용자가 없을 때
     */
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
