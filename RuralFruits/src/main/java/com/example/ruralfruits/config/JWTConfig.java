package com.example.ruralfruits.config;

import com.example.ruralfruits.common.JWTInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JWTConfig implements WebMvcConfigurer {
    // 注入配置文件中的上传路径
    @Value("${files.upload.path}")
    private String uploadPath;

    // 拦截器配置（原有逻辑不变）
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/file/upload",
                        "/file/upload/**",
                        "/file/download",
                        "/file/download/**",
                        "/file/**",
                        "/api/**",
                        "/products/list",
                        "/products/*",
                        "/products/recommendList",
                        "/banner/all",
                        "/comment/all",
                        "/categories/all",
                        "/cart/list",
                        "/notice/*"

                );
    }

    // 注入 JWTInterceptor 实例
    @Bean
    public JWTInterceptor jwtInterceptor() {
        return new JWTInterceptor();
    }

    // 静态资源映射（图片访问路径配置）
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/api/**")
                .addResourceLocations("file:" + uploadPath);
    }
}