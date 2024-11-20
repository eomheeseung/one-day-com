package example.in_continue_dev.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import example.in_continue_dev.domain.member.repository.MemberRepository;
import example.in_continue_dev.ex.customException.NotFoundTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtValidHandler extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final MemberRepository memberRepository;
    private final TokenParser tokenParser;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> optionalParseToken = tokenParser.tokenParse(request);

        if (optionalParseToken.isPresent()) {
            try {
                String token = optionalParseToken.get();

                // 토큰 검증
                DecodedJWT decodedJWT =
                        JWT.require(Algorithm.HMAC256(jwtProperties.getSecret().getBytes())).build().verify(token);

                // 토큰이 유효하면, Authentication 객체를 SecurityContext에 설정
                String username = decodedJWT.getSubject();
                UserDetails userDetails = new User(username, "", new ArrayList<>());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JWTVerificationException ex) {
                // 토큰이 만료되었거나 유효하지 않은 경우 401 Unauthorized 응답을 보냄
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired or invalid");
                return;
            }
        } else {
            throw new NotFoundTokenException("토큰이 존재하지 않습니다.");
        }

        filterChain.doFilter(request, response);
    }
}
