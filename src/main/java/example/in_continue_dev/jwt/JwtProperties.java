package example.in_continue_dev.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Slf4j
public class JwtProperties {
    private String secret;
    private long expiration;
    private long refreshExpiration;
    private String issuer;

    @PostConstruct
    public void init() {
        log.info("JWT Properties Loaded: secret={}, expiration={}, refreshExpiration={}, issuer={}",
                secret, expiration, refreshExpiration, issuer);
    }
}
