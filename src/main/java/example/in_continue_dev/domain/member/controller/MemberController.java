package example.in_continue_dev.domain.member.controller;

import example.in_continue_dev.domain.dto.MemberResponseDto;
import example.in_continue_dev.domain.member.MemberService;
import example.in_continue_dev.jwt.JwtService;
import example.in_continue_dev.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final UserService userService;
    private final JwtService jwtService;
    private final MemberService memberService;


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

    @PostMapping("/api/user/details")
    public ResponseEntity<MemberResponseDto> getUserDetails(Authentication authentication) {

        log.info("details controller call");
        // 인증된 사용자의 정보를 가져옴
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 이메일 또는 사용자 이름을 추출
        // UserDetails의 getUsername()을 통해 이메일 또는 사용자 ID를 얻음
        String email = userDetails.getUsername();

        // 이메일을 기반으로 사용자 정보를 데이터베이스에서 조회
        MemberResponseDto memberResponseDto = memberService.findMemberByEmail(email);

        // 사용자 정보 반환
        return ResponseEntity.ok(memberResponseDto);
    }

    @GetMapping("/api/health-check")
    public ResponseEntity<String> checkHealth(Authentication authentication) {
        log.info("health check controller call");

        // 인증된 사용자 정보 로그 기록 (필요 시)
        if (authentication != null && authentication.isAuthenticated()) {

            /*
            TODO
             principal은 어떤 객체타입이 있는지
             여기서는 UserDetails로 객체타입을 바꿈.
             UserDetails 객체 안에는 어떠한 값들이 있는지
             */
            Object principal = authentication.getPrincipal();
            String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
            log.info("Authenticated user: {}", username);
        } else {
            log.warn("No authenticated user found.");
        }

        return ResponseEntity.ok("ok");
    }

}
