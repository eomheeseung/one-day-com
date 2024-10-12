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

        // token logging
        log.info("accessToken:{}", accessToken.getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // CustomOauth2User로 네이버 정보 처리

        return new CustomOauth2User(oAuth2User);
    }
}
