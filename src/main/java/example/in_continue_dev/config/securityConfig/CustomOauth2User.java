package example.in_continue_dev.config.securityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.lang.annotation.Repeatable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CustomOauth2User implements OAuth2User{
    private final OAuth2User oAuth2User;

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

    // java.lang.IllegalArgumentException: principalName cannot be empty
    // getName()이 빌 때 발생한다고 함
    @Override
    public String getName() {
        // 네이버의 사용자 정보에서 'id' 값을 사용자 식별자로 사용
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return (String) response.get("id");  // 'id'는 네이버에서 제공하는 고유 식별자
    }

    public Map<String, Object> getUserInfo() {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        Object nickname = response.get("name");
        Object email = response.get("email");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", nickname);
        userInfo.put("email", email);

        return userInfo;
    }
}
