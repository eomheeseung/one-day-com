package example.in_continue_dev.config.securityConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.lang.annotation.Repeatable;
import java.util.*;

@Slf4j
public class CustomOauth2User implements OAuth2User {
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

        log.info("attributes: {}", attributes.toString());

        Object response = attributes.get("response");

        // 타입이 LinkedHashMap인지를 먼저 확인
        if (response instanceof LinkedHashMap) {
            // Java에서 제네릭 타입의 런타임 확인은 불가능하므로, instanceof로 타입을 확인하고 unchecked cast 경고는 @SuppressWarnings("unchecked")로 해결
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response;
            // 이제 responseMap을 안전하게 사용할 수 있습니다.
            log.info("Successfully casted response to LinkedHashMap");

            Object name = responseMap.get("name");
            log.info("name: {}", name.toString());

            Object email = responseMap.get("email");
            Object mobile = responseMap.get("mobile");

            Map<String, Object> userInfo = new HashMap<>();

            // TODO mysql에 저장될 때 한글이 ???로 깨짐
            userInfo.put("name", name);
            userInfo.put("email", email);
            userInfo.put("mobile", mobile);

            return userInfo;
        } else {
            log.warn("response is not an instance of LinkedHashMap, actual type: {}", response.getClass().getName());
            throw new RuntimeException("response is not an instance of LinkedHashMap, actual type");
        }


    }
}
