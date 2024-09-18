package com.company.tathminiv2.rest.config;

import io.jmix.core.JmixSecurityFilterChainOrder;
import io.jmix.security.util.JmixHttpSecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(JmixSecurityFilterChainOrder.CUSTOM)
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**", "/users/**")
                .authorizeHttpRequests(requests -> requests
                                .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        JmixHttpSecurityUtils.configureAnonymous(http);

        return http.build();
    }
}