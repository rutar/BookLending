package com.example.booklending.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disables CSRF protection
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()  // Permit access to Swagger UI
                        .requestMatchers("/api/books/**").hasAnyRole("USER", "ADMIN")  // Secures API endpoints
                        .anyRequest().authenticated()  // Requires authentication for other requests
                )
                .httpBasic(Customizer.withDefaults());  // Configures HTTP Basic Authentication

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
        userDetailsService.createUser(User.withUsername("user")
                .password("{noop}password")  // {noop} is used to indicate that no password encoding is applied
                .roles("USER")
                .build());
        userDetailsService.createUser(User.withUsername("admin")
                .password("{noop}adminpass")  // {noop} is used to indicate that no password encoding is applied
                .roles("USER", "ADMIN")
                .build());
        return userDetailsService;
    }
}
