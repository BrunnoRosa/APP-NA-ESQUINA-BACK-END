package com.example.app_na_esquina.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Libera todos os endpoints da API
                .allowedOrigins("*") // Permite requisições de qualquer origem (útil para desenvolvimento mobile)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Permite estes verbos HTTP
                .allowedHeaders("*"); // Permite qualquer cabeçalho (incluindo o nosso Authorization com Token)
    }
}