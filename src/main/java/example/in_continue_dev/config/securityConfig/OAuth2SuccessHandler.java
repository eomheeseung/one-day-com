package example.in_continue_dev.config.securityConfig;

import example.in_continue_dev.domain.member.Member;
import example.in_continue_dev.domain.member.repository.MemberRepository;
import example.in_continue_dev.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final Oauth2Service oauth2Service;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(), token.getName());

        OAuth2AccessToken oauth2AccessToken = authorizedClient.getAccessToken();
        OAuth2UserRequest userRequest = new OAuth2UserRequest(
                authorizedClient.getClientRegistration(), oauth2AccessToken);

        OAuth2User oAuth2User = oauth2Service.loadUser(userRequest);
        String email = ((CustomOauth2User) oAuth2User).getEmail();
        String contact = ((CustomOauth2User) oAuth2User).getContact();
        String name = ((CustomOauth2User) oAuth2User).getUserName();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            Member member = new Member();
            member.setEmail(email);
            member.setName(name);
            member.setWorkArea("a");
            member.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            member.setContact(contact);

            memberRepository.save(member);
        }

        // JWT 발급
        String appAccessToken = jwtService.generateAccessToken(email);
        String appRefreshToken = jwtService.generateRefreshToken();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", appAccessToken);
        tokens.put("refreshToken", appRefreshToken);

        log.info("자체 어플리케이션 access token 발급:{}", appAccessToken);
        log.info("자체 어플리케이션 refresh token 발급:{}", appRefreshToken);


//        response.sendRedirect("http://localhost:3000/?accessToken=" + accessToken + "&refreshToken=" + refreshToken);
//        response.sendRedirect("http://localhost:8080/swagger-ui/");

//        response.sendRedirect("http://localhost:8080/index");

    }
}

