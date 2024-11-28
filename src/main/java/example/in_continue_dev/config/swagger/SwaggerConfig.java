package example.in_continue_dev.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    // Swagger UI에서 OAuth2 인증을 설정하는 클래스
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)  // HTTP 방식으로 Bearer Token을 사용
                                        .scheme("bearer")  // Bearer Token 방식 사용
                                        .bearerFormat("JWT")  // JWT 형식의 Bearer Token
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("JWT"));  // JWT를 보안 요구사항에 추가
    }


}
