package example.in_continue_dev.config.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;
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

        // OAuth2 인증 성공 시 OAuth2AuthenticationToken 가져오기
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        // AuthorizedClient 가져오기
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(), token.getName());

        // AccessToken 가져오기
        OAuth2AccessToken oauth2AccessToken = authorizedClient.getAccessToken();

        // OAuth2UserRequest 생성
        OAuth2UserRequest userRequest = new OAuth2UserRequest(
                authorizedClient.getClientRegistration(), oauth2AccessToken);

        // loadUser 호출하여 사용자 정보 로드
        OAuth2User oAuth2User = oauth2Service.loadUser(userRequest);

        String email = ((CustomOauth2User) oAuth2User).getEmail();
        String contact = ((CustomOauth2User) oAuth2User).getContact();

        Optional<Member> optionalMember = memberRepository.findByLoginId(email);

        if (optionalMember.isEmpty()) {
            request.getSession().setAttribute("email", email);
            request.getSession().setAttribute("contact", contact);

            // 세션에 저장된 정보를 로그로 확인
            log.info("Session email: {}", request.getSession().getAttribute("email"));
            log.info("Session contact: {}", request.getSession().getAttribute("contact"));

            // 리디렉션 - get이 호출
            response.sendRedirect("http://localhost:3000/Oauth2InputForm");
        } else {
            Member member = optionalMember.get();

            // JWT 생성
            String generateAccessToken = jwtProvider.generateAccessToken(member.getLoginId());
            String generateRefreshToken = jwtProvider.generateRefreshToken();

            log.info("Generated access JWT: {}", generateAccessToken);
            log.info("Generated refresh JWT: {}", generateRefreshToken);

            // JWT를 JSON 형태로 클라이언트에 응답
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", generateAccessToken);
            tokens.put("refreshToken", generateRefreshToken);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(tokens));

            // CORS 설정 (필요한 경우)
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            response.setHeader("Access-Control-Allow-Credentials", "true"); // 쿠키 전송 허용

            response.sendRedirect("http://localhost:3000/tokenHandler");
        }
    }
}
