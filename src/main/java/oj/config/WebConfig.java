package oj.config;

import oj.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类：配置拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加Token拦截器，拦截所有请求
        registry.addInterceptor(tokenInterceptor)
                // 拦截管理员测试题目相关接口
                .addPathPatterns("/api/testQuestion/**")
                // 拦截用户测试题目相关接口
                .addPathPatterns("/api/user/testQuestion/**")
                // 拦截评论相关接口
                .addPathPatterns("/api/comment/**")
                // 排除登录和注册接口（这些接口不需要验证token）
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/user/register");
    }
}
