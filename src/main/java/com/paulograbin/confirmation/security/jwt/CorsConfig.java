package com.paulograbin.confirmation.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
public class CorsConfig {

    @Bean
    public CorsConfiguration corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("https://paulograbin.github.io/**");

        return corsConfiguration;
    }

}
