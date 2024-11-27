package example.in_continue_dev.config.securityConfig;

import example.in_continue_dev.domain.member.repository.MemberRepository;
import example.in_continue_dev.jwt.JwtService;
import example.in_continue_dev.jwt.JwtValidHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] NOCHECK = new String[]{"/login/**",
            "/oauth2/**", "/signIn", "/signUp",
            "/api/Oauth2InputForm"};
    private final Oauth2Service oauth2Service;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final JwtValidHandler jwtValidHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                // session policy
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(configurator ->
                        configurator.requestMatchers(NOCHECK).permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(customizer -> customizer.successHandler(loginSuccessFilter())
//                        .authorizationEndpoint(authorizationEndpointCustomizer ->
//                                authorizationEndpointCustomizer.baseUri("/oauth2/authorization/**"))
                )

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
                .addFilterBefore(jwtValidHandler, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationSuccessHandler loginSuccessFilter() {
        return new OAuth2SuccessHandler(oauth2Service, authorizedClientService, memberRepository, jwtService, passwordEncoder());
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // React 앱의 도메인 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 요청에 대해 CORS 설정 적용

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

