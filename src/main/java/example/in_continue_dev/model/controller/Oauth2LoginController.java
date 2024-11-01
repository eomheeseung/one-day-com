package example.in_continue_dev.model.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class Oauth2LoginController {


    /**
     * front에서 url로 호출하면 oauth2 로직 실행
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/api/oauth2/authorization/naver")
    public void oauth2Transfer(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:8080/oauth2/authorization/naver");
    }
}
