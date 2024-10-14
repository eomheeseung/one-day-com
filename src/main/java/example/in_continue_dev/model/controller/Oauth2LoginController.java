package example.in_continue_dev.model.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class Oauth2LoginController {

    /**
     * front에서 해당 uri를 get으로 받아서 인증을 8080으로 포워딩해서 보냄
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/oauth2/login/naver")
    public ResponseEntity<HttpStatus> authorizationNaver(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("http://localhost:8080/oauth2/authorization/naver");
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
