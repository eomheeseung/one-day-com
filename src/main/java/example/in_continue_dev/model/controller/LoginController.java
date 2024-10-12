package example.in_continue_dev.model.controller;

import example.in_continue_dev.domain.User.dto.Oauth2UserInputDTO;
import example.in_continue_dev.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @PostMapping("/oauth2InputForm")
    public String handleOauth2Input(@ModelAttribute Oauth2UserInputDTO oauth2UserInputDTO,
                                    HttpServletRequest request) {
        // 입력받은 name과 role을 처리 (예: DB에 저장 등)

        Object email = request.getSession().getAttribute("email");
        Object contact = request.getSession().getAttribute("contact");

        log.info("http session get email:{}", email.toString());
        log.info("http session get contact:{}", contact.toString());

        userService.Oauth2SaveUser(oauth2UserInputDTO, request);

        // signIn 페이지로 리디렉션
        return "redirect:/signIn"; // "/signIn" 페이지로 리디렉션
    }

    // 회원가입 처리 메서드
//    @PostMapping("/signUp")
//    public String handleSignUp(@ModelAttribute Oauth2UserInputDTO oauth2UserInputDTO, Model model) {
//        try {
//            // 사용자 저장 로직 (예: userService.saveUser(userInputDTO))
//            userService.saveUser(oauth2UserInputDTO);
//            return "redirect:/signIn"; // 성공적으로 저장된 경우 signIn 페이지로 리디렉션
//        } catch (RuntimeException e) {
//            model.addAttribute("errorMessage", e.getMessage()); // 에러 메시지를 모델에 추가
//            return "signUp"; // 에러가 발생한 경우 다시 signUp 페이지로
//        }
//    }
}
