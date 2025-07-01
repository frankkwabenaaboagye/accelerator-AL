package org.secureaid.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SecureAid Donation API")
                        .version("1.0.0")
                        .description("API for secure, auditable, and concurrent emergency donations.")
                        .contact(new Contact().name("SecureAid Team").email("support@secureaid.org"))
                );
    }
} 