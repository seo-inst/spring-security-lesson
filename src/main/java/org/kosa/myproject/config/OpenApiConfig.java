package org.kosa.myproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Swagger 정의의 핵심: JWT 인증 방식을 Swagger에 알려주는 설정
    @Bean
    public OpenAPI openAPI() {
        // 1. SecurityScheme 이름 정의
        String jwtSchemeName = "JWT Token";

        // 2. SecurityRequirement 추가 - 모든 API에 JWT 인증 적용
        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(jwtSchemeName);

        // 3. SecurityScheme 상세 정의
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)  // SecurityScheme 이름
                        .type(SecurityScheme.Type.HTTP)  // HTTP 인증 방식
                        .scheme("bearer")  // Bearer 토큰 사용
                        .bearerFormat("JWT"));  // JWT 형식

        // 4. OpenAPI 문서 구성
        return new OpenAPI()
                .info(new Info()
                        .title("API 문서")  // API 문서 제목
                        .description("Spring Security + JWT + Swagger 연동")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)  // 전역 Security 적용
                .components(components);  // Components에 SecurityScheme 등록
    }
}