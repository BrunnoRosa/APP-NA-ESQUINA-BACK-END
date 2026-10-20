package com.example.app_na_esquina.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API - Na Esquina")
                        .version("1.0.0")
                        .description("Documentação interativa da API do aplicativo Na Esquina - TCC SENAI.")
                        .contact(new Contact()
                                .name("Equipe Na Esquina")
                                .email("contato@naesquina.com")))
                // Adiciona a exigência de segurança globalmente
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Configura como o botão "Authorize" vai funcionar (esperando um Bearer JWT)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}