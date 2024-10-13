package example.in_continue_dev.model.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import example.in_continue_dev.domain.Member;
import example.in_continue_dev.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
/**
 * TODO
 *  1. 일반적인 회원가입의 경우
 *  1-1. inputForm.html 사용
 */
public class LoginViewController {
    private final JwtProvider jwtProvider;


    // signIn.html 페이지로 매핑
    @GetMapping("/signIn")
    public String signInPage() {

        // "/"가 없으면 "templates/" 경로 아래의 html 자체의 이름을 찾아서 반환한다.
        return "signIn";
    }

    @GetMapping("/oauth2InputForm")
    public String inputForm(HttpServletRequest request) {

        log.info("get mapping session :{}", request.getSession().getAttribute("email").toString());

        return "oauth2InputForm";
    }

    // 회원가입 페이지를 보여주는 메서드
    @GetMapping("/signUp")
    public String signUpPage() {
        return "signUp";
    }


    // 여기서 해당 controller에서 token을 발행해야 함.
    @GetMapping("/main")
    public String mainPage(HttpServletRequest request, Model model) {
        String token = request.getHeader("Authorization");


        // TODO 이 부분이 null이 발생함.
        log.info("main page token 유효성 검증 :{}", token);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거

            try {
                // JWT 유효성 검증
                DecodedJWT decodedJWT = jwtProvider.validateAccessToken(token);
                String email = decodedJWT.getSubject(); // 클레임에서 이메일 가져오기

                log.info("getSubject email :{}", email);

                Member member = (Member) request.getSession().getAttribute("member");

                log.info("세션내부의 member:{}", member); // 로그 추가

                // 모델에 사용자 정보 추가
                if (member != null) {
                    model.addAttribute("name", member.getName());
                    model.addAttribute("workArea", member.getWorkArea());
                } else {
                    model.addAttribute("error", "Member not found in session");
                }

            } catch (Exception e) {
                // JWT가 유효하지 않거나 만료된 경우 처리
                model.addAttribute("error", "Invalid token");
            }
        } else {
            model.addAttribute("error", "No token provided");
        }

        return "main"; // main.html로 반환
    }

}
