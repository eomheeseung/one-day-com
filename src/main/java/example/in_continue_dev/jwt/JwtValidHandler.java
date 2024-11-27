package example.in_continue_dev.jwt;

import example.in_continue_dev.ex.customException.NotFoundTokenException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtValidHandler extends OncePerRequestFilter {
    private final JwtService jwtService;
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
                String subjectFromToken = jwtService.getSubjectFromToken(token);

                // 토큰 검증
                Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails existUserDetails = (UserDetails) authentication.getPrincipal();
                    String subject = existUserDetails.getUsername();

                    // 토큰의 subject가 같으냐
                    if (subject.equals(subjectFromToken)) {
                        // 토큰 만료 검증
                        if (jwtService.validateAccessToken(token)) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired");
                            return;
                        }

                        // 인증 정보가 같으면 필터 진행
                        filterChain.doFilter(request, response);
                        return;
                    } else {
                        // 인증 정보가 다르면 새로운 인증 객체 생성
                        List<GrantedAuthority> authorities = new ArrayList<>();  // 권한 부여 처리 필요

                        // UserDetails 객체를 사용하지 않고, custom 객체를 사용한다면,
                        // UserDetails 객체를 상속받아야 한다.
                        UserDetails userDetails = new User(subjectFromToken, "", authorities); // 빈 문자열 패스워드 사용

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        // 새로운 인증 정보 세팅
                        // usernamePasswordAuthenticationToken 클래스는 인증객체(authentication)의 대표적인 하위클래스이다.
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } else {
                    // 인증 정보가 없거나 잘못된 경우
                    throw new NotFoundTokenException("토큰이 존재하지 않습니다.");
                }

            } catch (JwtException ex) {
                // 토큰이 만료되었거나 유효하지 않은 경우 401 Unauthorized 응답을 보냄
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired or invalid");
                return;
            }
        } else {
            // 토큰이 존재하지 않으면 예외 처리
            throw new NotFoundTokenException("토큰이 존재하지 않습니다.");
        }

        filterChain.doFilter(request, response); // 필터 체인 계속 진행
    }

}
