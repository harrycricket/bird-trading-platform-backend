package com.gangoffive.birdtradingplatform.config;

import com.gangoffive.birdtradingplatform.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.gangoffive.birdtradingplatform.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.gangoffive.birdtradingplatform.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.gangoffive.birdtradingplatform.security.oauth2.RestAuthenticationEntryPoint;
import com.gangoffive.birdtradingplatform.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.gangoffive.birdtradingplatform.enums.Permission.*;
import static com.gangoffive.birdtradingplatform.enums.UserRole.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AppProperties appProperties;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
    private static final String[] WHITE_LIST_URLS = {
            "/api/v1/auth/**",
            "/",
            "/auth/**",
            "/oauth2/**",
            "/error",
            "/user/me",
            "/v1/birds",
            "/v1/birds/**",
            "/v1/accessories",
            "/v1/accessories/**",
//            "/favicon.ico",
//            "/**/*.png",
//            "/**/*.gif",
//            "/**/*.svg",
//            "/**/*.jpg",
//            "/**/*.html",
//            "/**/*.css",
//            "/**/*.js",
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
    private final long MAX_AGE_SECS = 3600;

    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(appProperties.getCors().getAllowedOrigins())
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(MAX_AGE_SECS);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new RestAuthenticationEntryPoint()))
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
                .oauth2Login(oauth2 -> oauth2.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.baseUri("/oauth2/authorize").authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                        .redirectionEndpoint(redirectionEndpoint -> redirectionEndpoint.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
}
