package example.in_continue_dev.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class TokenParser {
    public Optional<String> tokenParse(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        return Optional
                .ofNullable(authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : null);
    }
}
