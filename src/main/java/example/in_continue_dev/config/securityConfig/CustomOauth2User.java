package example.in_continue_dev.config.securityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.lang.annotation.Repeatable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class CustomOauth2User implements OAuth2User{
    private OAuth2User oAuth2User;

    public CustomOauth2User(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return "";
    }

    public String getEmail() {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        Object nickname = response.get("nickname");
        Object email = response.get("email");
        response.get()
    }
}
