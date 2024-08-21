package com.org.vitaproject.config;


import com.org.vitaproject.filter.JwtAuthenticationFilter;
import com.org.vitaproject.service.impl.UserDetailsServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsServiceImp userDetailsServiceImp;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(req ->
                        req.requestMatchers( "/login/**", "/register/**",
                                        "/verify/**","/send-verification-email", "/test/**", "/api/**").permitAll().
                                requestMatchers("/users/auth/add-bigdata-profile/**").hasAuthority("ADMIN").
                                requestMatchers("/users/auth/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers("/analysis/**").hasAuthority("BIGDATA").
                                requestMatchers("/Organization/**",
                                        "/Pharmacy/**", "/XRay-Lab/**").authenticated().
                                requestMatchers("/doctors/**").hasAuthority("DOCTOR").
                                requestMatchers("/patients/**").hasAuthority("PATIENT").
                                anyRequest().authenticated()).userDetailsService(userDetailsServiceImp)
                .sessionManagement(session -> session.sessionCreationPolicy
                        (SessionCreationPolicy.STATELESS)).
                addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


}
