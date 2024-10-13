package example.in_continue_dev.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtProvider {
    // JwtProperties 주입
    private final JwtProperties jwtProperties;

    // Algorithm을 선언하지만 초기화는 생성자에서
    private final Algorithm algorithm;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // 로그 추가
        log.info("JwtProperties issuer (yml 확인용.): {}", jwtProperties.getIssuer());
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret()); // JwtProperties에서 비밀 키 가져오기
    }

    // JWT 생성 (Access Token)
    public String generateAccessToken(String email) {
        return JWT.create()
                .withSubject(email) // 클레임 설정
                .withIssuedAt(new Date()) // 발급 시간
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration())) // 만료 시간
                .sign(algorithm); // 서명
    }

    // JWT 생성 (Refresh Token)
    public String generateRefreshToken() {
        return JWT.create()
                .withSubject(UUID.randomUUID().toString()) // 유니크한 식별자 (UUID) 사용
                .withIssuedAt(new Date()) // 발급 시간
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration())) // 만료 시간
                .sign(algorithm); // 서명
    }

    // JWT 검증
    public DecodedJWT validateAccessToken(String token) {
        JWTVerifier verifier =
                JWT.require(algorithm)
                        .build();
        return verifier.verify(token);
    }

    // Refresh Token 검증
    public DecodedJWT validateRefreshToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return verifier.verify(token);
    }
}
