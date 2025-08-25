package com.example.HospitalSystemBackend.Config;

import com.example.HospitalSystemBackend.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                // 1) CORS, CSRF 설정
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // 2) 세션 대신 JWT
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3) URL별 권한
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 (React 파일들) 허용
                        .requestMatchers("/", "/static/**", "/*.js", "/*.css", "/*.ico", "/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.svg").permitAll()
                        // 로그인·검증만 공개
                        .requestMatchers("/api/auth/**","/api/auth/login", "/api/auth/validate", "/api/auth/departments", "/api/admin/**").permitAll()
                        // OPTIONS preflight도 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        // 나머지 API는 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        // React Router를 위한 모든 경로 허용 (API가 아닌 경우)
                        .anyRequest().permitAll()
                )
                // 4) JWT 필터
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    // CORS 설정 빈
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 통합 배포: 같은 서버에서 서빙되므로 기본적으로 CORS 문제 없음
        // 하지만 안전을 위해 현재 서버 도메인 허용
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 및 인증 헤더 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // /api/** 경로에 CORS 적용
        return source;
    }
}