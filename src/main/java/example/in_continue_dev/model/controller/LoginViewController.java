package example.in_continue_dev.model.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
/**
 * TODO
 *  1. 일반적인 회원가입의 경우
 *  1-1. inputForm.html 사용
 */
public class LoginViewController {
    // signIn.html 페이지로 매핑
    @GetMapping("/signIn")
    public String signInPage() {
        return "signIn"; // templates/signIn.html을 반환
    }

    @GetMapping("/oauth2InputForm")
    public String inputForm(HttpServletRequest request) {

        log.info("get mapping session :{}", request.getSession().getAttribute("email").toString());

        return "oauth2InputForm"; // templates/inputForm.html을 반환
    }

    // 회원가입 페이지를 보여주는 메서드
    @GetMapping("/signUp")
    public String signUpPage() {
        return "signUp"; // templates/signUp.html을 반환
    }
}
