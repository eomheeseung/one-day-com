package example.in_continue_dev.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import example.in_continue_dev.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class JwtService {
    // JwtProperties 주입
    private final JwtProperties jwtProperties;

    // Algorithm을 선언하지만 초기화는 생성자에서
    private final Algorithm algorithm;
    private final UserService userService;

    public JwtService(JwtProperties jwtProperties, UserService userService) {
        this.jwtProperties = jwtProperties;
        // 로그 추가
        log.info("JwtProperties issuer (yml 확인용.): {}", jwtProperties.getIssuer());
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret()); // JwtProperties에서 비밀 키 가져오기
        this.userService = userService;
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

    // jwt 검증
    public boolean validateAccessToken(String token) {
        // 만료시간도 검증이 된다.
        try {
            JWTVerifier verifier =
                    JWT.require(algorithm).build();
            verifier.verify(token);
            return true; // 검증 성공 시 true 반환
        } catch (JWTVerificationException e) {
            return false; // 검증 실패 시 false 반환
        }
    }

    public String getSubjectFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }


    // Refresh Token을 사용해 새로운 Access Token 및 Refresh Token 발급
    public Map<String, String> refreshTokens(String refreshToken) {
        try {
            // Refresh Token 검증
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(refreshToken);

            // Refresh Token 유효성 검사
            Date expirationDate = decodedJWT.getExpiresAt();

            if (expirationDate.before(new Date())) {
                throw new RuntimeException("Refresh token expired");
            }

            // 새로운 Access Token 생성
            String newAccessToken = generateAccessToken(decodedJWT.getSubject());

            // 새로운 Refresh Token 생성 (선택적으로 새로운 Refresh Token을 발급)
            String newRefreshToken = generateRefreshToken();

            // 새로운 토큰들을 반환
            Map<String, String> response = new HashMap<>();
            response.put("newAccessToken", newAccessToken);
            response.put("newRefreshToken", newRefreshToken);

            UserDetails userDetails = new User(decodedJWT.getSubject(), "", new ArrayList<>()); // 필요한 경우 권한 추가

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return response;

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid refresh token");
        }
    }


}
