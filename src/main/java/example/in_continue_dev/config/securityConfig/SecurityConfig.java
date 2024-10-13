package example.in_continue_dev.config.securityConfig;

import example.in_continue_dev.domain.repository.MemberRepository;
import example.in_continue_dev.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] NOCHECK = new String[]{"/login/**", "/oauth2/**", "/signIn"};
    private final Oauth2Service oauth2Service;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(configurator ->
                        configurator.requestMatchers(NOCHECK).permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(customizer -> customizer.successHandler(loginSuccessFilter()))
                .formLogin(configurator -> configurator
                        .loginPage("/signIn")
                        .permitAll()
                        .failureUrl("/signIn?error=true"))


                .logout(customizer -> customizer
                        /*
                         logout을 요청받을 url
                         1. main page에서 logout button을 누르면 /logout를 호출
                         2. 해당 /logout을 여기서 받아서 logout을 spring security가 안전하게 실행 후
                         3. 아래의 successUrl 호출
                         */
                        .logoutUrl("/logout")
                        // logout을 수행한 후 실행할 url
                        .logoutSuccessUrl("/signIn"))
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessFilter() {
        return new OAuth2SuccessHandler(oauth2Service, authorizedClientService, memberRepository, jwtProvider);
    }
}

