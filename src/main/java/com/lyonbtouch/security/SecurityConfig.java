package com.lyonbtouch.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/request-code", "/api/auth/verify-code").permitAll()
                
                // Manager endpoints
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/pending").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/*/approve").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/*").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/schedule-weeks").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/schedule-weeks/*/publish").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/shifts").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/shifts/*/shift-manager").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/shifts/*/requirements").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/schedule-entries").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/schedule-entries/*").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/swaps/*/approve").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/swaps/*/reject").hasRole("MANAGER")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
