package example.in_continue_dev.model.controller;

import example.in_continue_dev.domain.dto.MemberResponseDto;
import example.in_continue_dev.domain.member.MemberService;
import example.in_continue_dev.jwt.JwtService;
import example.in_continue_dev.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class Oauth2LoginController {
    private final UserService userService;
    private final JwtService jwtService;
    private final MemberService memberService;

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


    @GetMapping("/api/userInfo")
    public ResponseEntity<MemberResponseDto> getUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String token = authHeader.substring(7); // "Bearer " 부분 제거

        // JWT에서 이메일 추출
        String email = jwtService.getUsernameFromToken(token); // 유효성 검사를 필터에서 했으므로 이 부분에서 안전함

        MemberResponseDto memberResponseDto = memberService.findMemberByEmail(email);


        return ResponseEntity.ok(memberResponseDto); // 사용자 정보 반환
    }
}
