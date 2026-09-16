package com.alm.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import jakarta.servlet.http.HttpServletResponse;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
 @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
   return http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(a -> a
     .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/health").permitAll()
     .requestMatchers("/api/**").authenticated().anyRequest().permitAll())
     .httpBasic(Customizer.withDefaults())
     // Keep failed API authentication inside the application UI. The default
     // BasicAuthenticationEntryPoint triggers Chrome's intrusive native popup.
     .exceptionHandling(e -> e.authenticationEntryPoint((request, response, failure) -> {
       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
       response.setContentType("application/json");
       response.getWriter().write("{\"message\":\"Invalid email or password. Confirm the seeded user and restart the application.\"}");
     })).build();
 }
}
