package com.nhnacademy.ruleengineservice.config;

import com.nhnacademy.ruleengineservice.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 전역 CORS(Cross-Origin Resource Sharing) 설정 클래스입니다.
 * <p>
 * 이 클래스는 WebMvcConfigurer를 구현하여
 * 모든 엔드포인트에 대해 CORS 정책을 전역으로 적용합니다.
 * <ul>
 *   <li>허용할 Origin, HTTP Method, Header 등을 지정할 수 있습니다.</li>
 *   <li>기본적으로 모든 Origin과 Method를 허용하도록 설정할 수 있습니다.</li>
 *   <li>Controller 단위로 @CrossOrigin 어노테이션을 사용할 수도 있습니다.</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/v1/comfort/**",
                        "/api/v1/rules/**",
                        "/api/v1/rule-groups/**",
                        "/api/v1/rule-engine/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://aiot2.live") // 우리 도메인 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 기타 들어갈 method
                .allowedHeaders("*")
                .allowCredentials(true); // front 가 인증 헤더 보낸다면 true
    }
}
