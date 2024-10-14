package example.in_continue_dev.model.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import example.in_continue_dev.domain.Member;
import example.in_continue_dev.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
/**
 * TODO
 *  1. 일반적인 회원가입의 경우
 *  1-1. inputForm.html 사용
 */
public class LoginViewController {
    private final JwtProvider jwtProvider;

    // signIn.html 페이지를 보여주는 메서드
    @GetMapping("/signIn")
    public ResponseEntity<String> signInPage() {
        // signIn 페이지에 대한 정보를 JSON 형태로 반환
        return ResponseEntity.ok("signIn page loaded.");
    }

    @GetMapping("/oauth2InputForm")
    public ResponseEntity<String> inputForm(HttpServletRequest request) {
        log.info("get mapping session: {}", request.getSession().getAttribute("email"));

        // oauth2InputForm 페이지에 대한 정보를 JSON 형태로 반환
        return ResponseEntity.ok("oauth2InputForm page loaded.");
    }

    // 회원가입 페이지를 보여주는 메서드
    @GetMapping("/signUp")
    public ResponseEntity<String> signUpPage() {
        // signUp 페이지에 대한 정보를 JSON 형태로 반환
        return ResponseEntity.ok("signUp page loaded.");
    }

    @GetMapping("/main")
    public ResponseEntity<?> mainPage(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        log.info("main page token 유효성 검증: {}", token);

        if (token != null && token.startsWith("Bearer ")) {
            String parseToken = token.substring(7); // "Bearer " 부분을 제거

            try {
                // JWT 유효성 검증
                DecodedJWT decodedJWT = jwtProvider.validateAccessToken(parseToken);
                String email = decodedJWT.getSubject(); // 클레임에서 이메일 가져오기

                log.info("jwt getSubject email: {}", email);

                Member member = (Member) request.getSession().getAttribute("member");

                // 회원 정보가 있는 경우 JSON 형태로 반환
                if (member != null) {
                    return ResponseEntity.ok(new MemberResponse(member.getName(), member.getWorkArea()));
                } else {
                    return ResponseEntity.badRequest().body("Member not found in session");
                }

            } catch (Exception e) {
                // JWT가 유효하지 않거나 만료된 경우 처리
                log.error("Token validation error: {}", e.getMessage());
                return ResponseEntity.badRequest().body("Invalid token");
            }
        } else {
            return ResponseEntity.badRequest().body("No token provided");
        }
    }

    // 내부 클래스를 통해 회원 정보를 반환하는 DTO 정의
    private static class MemberResponse {
        private final String name;
        private final String workArea;

        public MemberResponse(String name, String workArea) {
            this.name = name;
            this.workArea = workArea;
        }

        public String getName() {
            return name;
        }

        public String getWorkArea() {
            return workArea;
        }
    }
}
