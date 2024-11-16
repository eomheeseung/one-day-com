package example.in_continue_dev.jwt;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshAccessToken(@RequestBody Map<String, String> refreshTokenRequest,
                                                     HttpServletResponse response) {

        String refreshToken = refreshTokenRequest.get("refreshToken");

        try {
            Map<String, String> tokens = jwtService.refreshTokens(refreshToken);
            String newRefreshToken = tokens.get("newAccessToken");
            String newAccessToken = tokens.get("newRefreshToken");

            response.addHeader("Authorization", "Bearer " + newAccessToken);


            return ResponseEntity.ok().body(newRefreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
    }
}
