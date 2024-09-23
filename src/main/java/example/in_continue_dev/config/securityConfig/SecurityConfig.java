package example.in_continue_dev.config.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.beans.BeanProperty;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] NOCHECK = new String[]{"/login/**", "/oauth2/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(configurator ->
                        configurator.requestMatchers(NOCHECK).permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(customizer -> customizer.successHandler(loginSuccessFilter()))
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessFilter() {
        return new LoginSuccessFilter();
    }
}

