package com.example.yin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 仅允许本地开发前端来源，携带凭证时不允许使用通配符
                .allowedOrigins(
                        "http://localhost:8080", "http://127.0.0.1:8080",
                        "http://localhost:8081", "http://127.0.0.1:8081",
                        "http://localhost:8082", "http://127.0.0.1:8082",
                        "http://localhost:8888", "http://127.0.0.1:8888")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**");
    }
}
