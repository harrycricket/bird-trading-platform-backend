package com.gangoffive.birdtradingplatform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.service.JwtService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final String[] WHITE_LIST_URLS = {
            "/api/v1/auth/",
            "/auth/",
            "/oauth2/",
            "/error",
            "/user/me",
            "/api/v1/users/register",
            "/api/v1/users/authenticate",
            "/api/v1/users/reset-password",
            "/api/v1/users/verify/register",
            "/api/v1/users/verify/reset-password",
            "/api/v1/products",
            "/api/v1/products/",
            "/api/v1/birds",
            "/api/v1/birds/",
            "/api/v1/accessories",
            "/api/v1/accessories/",
            "/api/v1/foods",
            "/api/v1/foods/",
            "/upload",
            "/api/v1/info/",
            "/api/v1/users/get-cookie",
            "/api/v1/package-order",
            "/api/v1/promotions",
            "/api/v1/products/top-product"
    };

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestPath = request.getServletPath();
        log.info("requestPath{}", requestPath);
        boolean isWhitelisted = Arrays.stream(WHITE_LIST_URLS).anyMatch(s -> s.startsWith(requestPath));
        log.info("isWhitelisted {}", isWhitelisted);
        if (isWhitelisted) {
            filterChain.doFilter(request, response);
            return;
        }
//        if (request.getServletPath().contains("/api/v1/auth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            // Create the error response JSON object
            ErrorResponse errorResponse = ErrorResponse
                                                        .builder()
                                                        .errorMessage("Invalid token")
                                                        .errorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                                                        .build();

            // Convert the error response to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);

            // Set the response content type to application/json
            response.setContentType("application/json");

            // Write the JSON error response to the response body
            response.getWriter().write(jsonErrorResponse);
            response.getWriter().flush();
            return;
//            filterChain.doFilter(request, response);
//            return;
        }
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                System.out.println(userDetails.toString());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
