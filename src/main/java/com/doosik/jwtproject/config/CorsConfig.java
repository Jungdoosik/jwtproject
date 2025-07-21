package com.doosik.jwtproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 URL 허용
          .allowedOrigins("http://localhost:5173") // 프론트 주소
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용 메서드
          .allowedHeaders("*") // 모든 헤더 허용
          .allowCredentials(true); // 쿠키 등 자격 정보 허용 여부
      }
    };
  }
}