package com.zhvavyy.backend.web.security;

import com.zhvavyy.backend.web.jwt.JwtAuthenticationFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class SecurityConfiguration {

    JwtAuthenticationFilter jwtFilter;
    AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**").permitAll()

                        .requestMatchers("/users/delete/**", "/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login"))

                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                )
               .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
    }
}
