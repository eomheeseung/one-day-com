package example.in_continue_dev.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    // JwtProperties 주입
    private final JwtProperties jwtProperties;


    // JWT 생성 (Access Token)
    public String generateAccessToken(String email) {
        Date accessExpireDate = Date.from(Instant.now().plus(jwtProperties.getExpiration(), ChronoUnit.MINUTES));

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(accessExpireDate)
                // secret key를 sign 하는데 기존의 method는 deprecated 되었기 때문에 key 객체를 생성해서 사용한다.
                .signWith(generateKey(jwtProperties.getSecret()), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 생성 (Refresh Token)

    public String generateRefreshToken() {
        Date refreshExpireTime = Date.from(Instant.now().plus(jwtProperties.getRefreshExpiration(),
                ChronoUnit.MINUTES));


        return Jwts.builder()
                .setId(jwtProperties.getSecret())
                .setIssuer(jwtProperties.getIssuer())
                .setExpiration(refreshExpireTime)
                .compact();
    }

    // token 검증
    public boolean validateAccessToken(String token) {
        try {
            // 토큰을 파싱하여 Claims 객체를 가져온다
            // 서명 검증은 parseClaimsJws() 호출 시 자동으로 이루어진다
            Claims claims = generateJwtParser()
                    .parseClaimsJws(token)
                    .getBody();

            // 추가로 Claims 정보 사용 가능
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Expiration: " + claims.getExpiration());

            return true; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 예외 처리
            System.out.println("만료된 토큰: " + e.getMessage());
        } catch (MalformedJwtException e) {
            // 잘못된 형식의 토큰 예외 처리
            System.out.println("잘못된 형식의 토큰: " + e.getMessage());
        } catch (SignatureException e) {
            // 서명 검증 실패 예외 처리
            System.out.println("서명 검증 실패: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 잘못된 토큰 예외 처리
            System.out.println("잘못된 토큰: " + e.getMessage());
        }

        return false; // 유효하지 않은 토큰
    }


    public String getSubjectFromToken(String token) {
        Claims claims = generateJwtParser().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


    // Refresh Token을 사용해 새로운 Access Token 및 Refresh Token 발급

    public Map<String, String> refreshTokens(String refreshToken) {
        HashMap<String, String> tokens = new HashMap<>();


        Claims claims = generateJwtParser()
                .parseClaimsJws(refreshToken)
                .getBody();

        Date expiration = claims.getExpiration();

        if (expiration.before(Date.from(Instant.now()))) {
            throw new ExpiredJwtException(null, claims, "Refresh Token has expired");
        }

        // Refresh Token이 유효하다면 새로운 토큰 발급
        String subject = claims.getSubject(); // 사용자의 식별자 (예: username, userId 등)
        String newAccessToken = generateAccessToken(subject);
        String newRefreshToken = generateRefreshToken();

        // 결과 반환
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }

    /*
    secret을 key 객체로 만들어서 반환한다.
     */
    private Key generateKey(String secret) {
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }


    private JwtParser generateJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret().getBytes())
                .build();
    }
}
