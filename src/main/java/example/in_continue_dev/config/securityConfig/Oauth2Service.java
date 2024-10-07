package example.in_continue_dev.config.securityConfig;

import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import example.in_continue_dev.domain.Role;
import example.in_continue_dev.domain.Users;
import example.in_continue_dev.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class Oauth2Service extends DefaultOAuth2UserService {
    private final UsersRepository usersRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /*
        디버그모드를 실행해서
        naver의 resposne를 보려면
        curl 명령어를 사용해서 (jq는 json을 예쁘게 보기위해서)

         curl -X GET "https://openapi.naver.com/v1/nid/me" \
              -H "Authorization: Bearer " | jq .

         확인해보자.
         */
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        log.info("accessToken:{}", accessToken.getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // CustomOauth2User로 네이버 정보 처리
        CustomOauth2User customOauth2User = new CustomOauth2User(oAuth2User);

        // 사용자 정보를 DB에 저장
        saveUser(customOauth2User.getUserInfo());

        return customOauth2User;
    }

    private void saveUser(Map<String, Object> userInfo) {
        String email = userInfo.get("email").toString();
        String name = userInfo.get("name").toString();
        String mobile = userInfo.get("mobile").toString();

        // 이메일로 사용자 조회 (이미 존재하는 사용자일 경우 저장하지 않음)
        Optional<Users> existingUser = usersRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            // 새로운 사용자 정보를 DB에 저장

            Users user = Users.builder()
                    .name(name)
                    .role(Role.ROLE_USERS)
                    .email(email)
                    .mobile(mobile)
                    .build();
            usersRepository.save(user);
        }
    }
}
