package example.in_continue_dev.model.controller;

import example.in_continue_dev.domain.User.dto.Oauth2UserInputDTO;
import example.in_continue_dev.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @PostMapping("/Oauth2InputForm")
    public ResponseEntity<String> handleOauth2Input(@RequestBody Oauth2UserInputDTO oauth2UserInputDTO,
                                                    HttpServletRequest request) {
        // 입력받은 name과 role을 처리 (예: DB에 저장 등)
        Object email = request.getSession().getAttribute("email");
        Object contact = request.getSession().getAttribute("contact");

        log.info("http session get email:{}", email != null ? email.toString() : "null");
        log.info("http session get contact:{}", contact != null ? contact.toString() : "null");


        userService.Oauth2SaveUser(oauth2UserInputDTO, request);

        // 성공적인 응답 반환
        // 이렇게 body에 string을 사용해도 http status 200과 함께 전달이 된다.
        return ResponseEntity.ok("User information saved successfully."); // 성공 메시지
    }

    // 회원가입 처리 메서드
    /*@PostMapping("/signUp")
    public ResponseEntity<String> handleSignUp(@RequestBody Oauth2UserInputDTO oauth2UserInputDTO) {
        try {
            // 사용자 저장 로직
            userService.saveUser(oauth2UserInputDTO);
            return ResponseEntity.ok("User registered successfully."); // 성공 메시지
        } catch (RuntimeException e) {
            log.error("Error during signup: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); // 에러 메시지
        }
    }*/
}
