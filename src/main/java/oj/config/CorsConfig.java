package oj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端源（你的前端地址：localhost:63342）
//        config.addAllowedOrigin("http://localhost:5057");

//        config.addAllowedOrigin("http://localhost:8081");

        config.addAllowedOrigin("*");
        // 允许 POST/GET 等请求方法
        config.addAllowedMethod("*");
        // 允许传递 JSON 等请求头
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 所有接口都允许跨域（也可指定 /mqtt/** 只对 MQTT 相关接口生效）
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}