package com.gangoffive.birdtradingplatform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.gangoffive.birdtradingplatform.enums.Permission.*;
import static com.gangoffive.birdtradingplatform.enums.UserRole.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private static final String[] WHITE_LIST_URLS = {
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(WHITE_LIST_URLS)
                                .permitAll()
                                .requestMatchers("/api/v1/admin/**").hasAnyRole(ADMIN.name())
                                .requestMatchers(GET, "/api/v1/admin/**").hasAnyAuthority(ADMIN_READ.name())
                                .requestMatchers(POST, "/api/v1/admin/**").hasAnyAuthority(ADMIN_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/admin/**").hasAnyAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/admin/**").hasAnyAuthority(ADMIN_DELETE.name())

                                .requestMatchers("/api/v1/shopowner/**").hasAnyRole(SHOPOWNER.name())
                                .requestMatchers(GET, "/api/v1/shopowner/**").hasAnyAuthority(SHOPOWNER_READ.name())
                                .requestMatchers(POST, "/api/v1/shopowner/**").hasAnyAuthority(SHOPOWNER_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/shopowner/**").hasAnyAuthority(SHOPOWNER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/shopowner/**").hasAnyAuthority(SHOPOWNER_DELETE.name())

                                .requestMatchers("/api/v1/user/**").hasAnyRole(USER.name())
                                .requestMatchers(GET, "/api/v1/user/**").hasAnyAuthority(USER_READ.name())
                                .requestMatchers(POST, "/api/v1/user/**").hasAnyAuthority(USER_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/user/**").hasAnyAuthority(USER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/user/**").hasAnyAuthority(USER_DELETE.name())

                                .requestMatchers("/api/v1/user/**").hasAnyRole(SHOPSTAFF.name())
                                .requestMatchers(GET, "/api/v1/user/**").hasAnyAuthority(SHOPSTAFF_READ.name())

                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
