package example.in_continue_dev.model.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import example.in_continue_dev.domain.Member;
import example.in_continue_dev.domain.User.dto.Oauth2UserInputDTO;
import example.in_continue_dev.domain.repository.MemberRepository;
import example.in_continue_dev.jwt.JwtProvider;
import example.in_continue_dev.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class Oauth2LoginController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @GetMapping("/oauth2/authorization/naver")
    public void oauth2Transfer(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:8080/oauth2/authorization/naver");
    }


    @PostMapping("/Oauth2InputForm")
    public ResponseEntity<String> handleOauth2Input(@RequestBody Oauth2UserInputDTO oauth2UserInputDTO,
                                                    HttpServletRequest request) {
        // 입력받은 name과 role을 처리 (예: DB에 저장 등)
        Object email = request.getSession().getAttribute("email");
        Object contact = request.getSession().getAttribute("contact");

        log.info("http session get email:{}", email != null ? email.toString() : "null");
        log.info("http session get contact:{}", contact != null ? contact.toString() : "null");


        userService.Oauth2SaveUser(oauth2UserInputDTO, request);

        // 이렇게 body에 string을 사용해도 http status 200과 함께 전달이 된다.
        return ResponseEntity.ok("User information saved successfully."); // 성공 메시지
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
    @PostMapping("/main")
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
