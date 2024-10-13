package example.in_continue_dev.config.securityConfig;

import example.in_continue_dev.domain.Member;
import example.in_continue_dev.domain.repository.MemberRepository;
import example.in_continue_dev.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final Oauth2Service oauth2Service;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        // Authentication에서 OAuth2AuthenticationToken 가져오기
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        // AuthorizedClient 가져오기
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(), token.getName());

        // AccessToken 가져오기
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        // OAuth2UserRequest 생성
        OAuth2UserRequest userRequest = new OAuth2UserRequest(
                authorizedClient.getClientRegistration(), accessToken);

        // loadUser 호출
        OAuth2User oAuth2User = oauth2Service.loadUser(userRequest);

        String email = ((CustomOauth2User) oAuth2User).getEmail();
        String contact = ((CustomOauth2User) oAuth2User).getContact();

        Optional<Member> optionalMember = memberRepository.findByLoginId(email);

        if (optionalMember.isEmpty()) {
            // null 발생하지 않음.
            request.getSession().setAttribute("email", email);
            request.getSession().setAttribute("contact", contact);

            // 세션에 저장된 정보를 로그로 확인
            log.info("Session email: {}", request.getSession().getAttribute("email").toString());
            log.info("Session contact: {}", request.getSession().getAttribute("contact").toString());

            // 리디렉션 - get이 호출
            response.sendRedirect("/oauth2InputForm");


        } else {
            Member member = optionalMember.get();

            String generateAccessToken = jwtProvider.generateAccessToken(member.getLoginId());
            String generateRefreshToken = jwtProvider.generateRefreshToken();

            log.info("generate access jwt: {}", generateAccessToken);
            log.info("generate refresh jwt: {}", generateRefreshToken);

            request.getSession().setAttribute("member", member);

            log.info("인증 후 member:{}", member.getName());

            response.addHeader("Authorization", "Bearer " + generateAccessToken);


            // 메인 페이지로 리다이렉션
            response.sendRedirect("/main");
        }
    }
}
