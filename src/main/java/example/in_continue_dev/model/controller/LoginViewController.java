package example.in_continue_dev.model.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import example.in_continue_dev.domain.Member;
import example.in_continue_dev.domain.repository.MemberRepository;
import example.in_continue_dev.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    private final MemberRepository memberRepository;

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

    @GetMapping("/login")
    public ResponseEntity<String> loginPage() {
        log.info("login page loaded.");
        return ResponseEntity.ok("login page loaded.");
    }


    /**
     * TODO 토큰유효성 검증
     *  파이프라인
     *  1. naver 버튼을 누르면 사용자의 정보를 입력받아야 하는데 자동으로 로그인이 되는문제
     *  2. naver 사용자 인증 후 oauth2InputForm에서 추가 정보를 받고 db에 저장 후 signIn으로 리다이렉트
     *  3. signIn으로 리다이렉트 후 naver 버튼을 다시 누르면 main으로 가져야 하는데 여기서 아래의 컨트롤러가 호출되지 않음
     *  3-1. successHandler에서 naver 인증 후 token은 발급
     *  3-2. token을 발급하고 header에 넣고, main으로 리다이렉트
     *  3-3. but 아래의 controller이 호출되지 않고, front도 signIn page만 보여짐 (토큰의 인증이 안된 것)
     *  * oauth2를 할 때 id, pw를 입력받게 -> logout를 구성하면 될 것 같음
     *
     *  10.15
     *  해당 메소드는 실행이 되는데
     *  렌더링이 되는 url쪽을 봐야할 것 같음 frontend 문제
     * @param request
     * @return
     */
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

                Optional<Member> optionalMember = memberRepository.findByLoginId(email);

                // 회원 정보가 있는 경우 JSON 형태로 반환
                if (optionalMember.isPresent()) {
                    Member member = optionalMember.get();
                    return ResponseEntity.ok(new MemberResponse(member.getName(), member.getWorkArea()));
                } else {
                    return ResponseEntity.badRequest().body("Member not found");
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
