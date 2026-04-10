package com.gong.modu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 모든 요청 일단 허용함
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 기본 로그인 폼 비활성화
                .formLogin(form -> form.disable())

                // HTTP Basic 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()

                );

        return httpSecurity.build();
    }
}
