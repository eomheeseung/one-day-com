package example.in_continue_dev.config.securityConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomOauth2User implements OAuth2User {
    private final OAuth2User oAuth2User;

    public CustomOauth2User(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
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

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes(); // 여기에 실제 속성을 반환하도록 수정
    }

    public String getContact() {
        return getUserAttribute("mobile");
    }

    public String getEmail() {
        return getUserAttribute("email");
    }

    public String getUserName() {
        return getUserAttribute("name");
    }

    private String getUserAttribute(String attributeName) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get(attributeName);
    }


}
