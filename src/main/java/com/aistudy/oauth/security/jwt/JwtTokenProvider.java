package com.aistudy.oauth.security.jwt;

import com.aistudy.oauth.security.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * HMAC-SHA 서명 기반 JWT 구현({@link TokenProvider}).
 *
 * <p><b>SRP</b>: 토큰의 생성·검증·복원만 담당한다. 누가 어떤 조건에서 토큰을 발급할지(정책)는
 * 성공 핸들러가, 언제 검증할지는 인증 필터가 결정한다.
 */
@Component
public class JwtTokenProvider implements TokenProvider {

    private static final String EMAIL_CLAIM = "email";
    private static final String ROLES_CLAIM = "roles";

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(JwtProperties properties) {
        byte[] keyBytes = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret 는 HS256 서명을 위해 최소 32바이트여야 합니다. 현재 " + keyBytes.length + "바이트");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = properties.expirationMs();
    }

    @Override
    public String createToken(UserPrincipal principal) {
        Instant now = Instant.now();
        String roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .subject(String.valueOf(principal.getUserId()))
                .claim(EMAIL_CLAIM, principal.getEmail())
                .claim(ROLES_CLAIM, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = parse(token);
        Long userId = Long.valueOf(claims.getSubject());
        String roles = Optional.ofNullable(claims.get(ROLES_CLAIM, String.class)).orElse("");
        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .filter(role -> !role.isBlank())
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();
        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
