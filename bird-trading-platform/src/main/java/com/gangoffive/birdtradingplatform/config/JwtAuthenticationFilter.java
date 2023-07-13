package com.gangoffive.birdtradingplatform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.entity.ShopStaff;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.enums.ShopOwnerStatus;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.exception.AuthenticateException;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.ShopOwnerRepository;
import com.gangoffive.birdtradingplatform.repository.ShopStaffRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ShopStaffRepository shopStaffRepository;
    private final AccountRepository accountRepository;
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
            "/api/v1/product/add-new",
            "/api/v1/info/",
            "/api/v1/users/get-cookie",
            "/api/v1/promotions",
            "/api/v1/products/top-product",
            "/api/v1/types/birds",
            "/api/v1/types/foods",
            "/api/v1/types/accessories",
            "/api/v1/reviews/products",
            "/kafka/test",
    };

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestPath = request.getServletPath();
        log.info("requestPath{}", requestPath);
        boolean isWhitelisted = Arrays.stream(WHITE_LIST_URLS).anyMatch(s -> requestPath.startsWith(s));
        log.info("isWhitelisted {}", isWhitelisted);
        if (isWhitelisted) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        String userEmail;
        String staffUserName;
        Long shopOwnerId;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);
            staffUserName = jwtService.extractStaffUsername(jwt);
            shopOwnerId = jwtService.extractShopOwnerId(jwt);
            log.info("staffUserName {}", staffUserName);
        } catch (Exception e) {
            responseExceptionWithJson(response, "Invalid token");
            return;
        }
        if (userEmail != null && staffUserName == null && shopOwnerId == null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                responseExceptionWithJson(response, e.getMessage());
                return;
            }
        }

        if (userEmail != null && staffUserName == null && shopOwnerId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetailsService shopOwnerDetailsService = username -> {
                    Account account = accountRepository.findByEmail(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Not found this account."));
                    if (account.getShopOwner().getStatus().equals(ShopOwnerStatus.BAN)) {
                        throw new AuthenticateException("Shop account has been banned..");
                    } else {
                        return UserPrincipal.create(account);
                    }
                };
                UserDetails userDetails = shopOwnerDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                responseExceptionWithJson(response, e.getMessage());
                return;
            }
        }

        if (userEmail != null && staffUserName != null
                && shopOwnerId == null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetailsService staffDetailsService = username -> {
                    ShopStaff staff = shopStaffRepository.findByUserName(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Not found this staff account."));
                    if (staff.getStatus().equals(AccountStatus.BANNED)) {
                        throw new AuthenticateException("Staff account has been banned.");
                    } else if (staff.getShopOwner().getStatus().equals(ShopOwnerStatus.BAN)) {
                        throw new AuthenticateException("Shop owner account has been banned.");
                    } else {
                        Account account = new Account();
                        account.setId(staff.getId());
                        account.setEmail(staff.getUserName());
                        account.setRole(UserRole.SHOPSTAFF);
                        account.setPassword(staff.getPassword());
                        return UserPrincipal.create(account);
                    }
                };
                UserDetails staffDetails = staffDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, staffDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            staffDetails,
                            null,
                            staffDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                responseExceptionWithJson(response, e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void responseExceptionWithJson(HttpServletResponse response, String e) throws IOException {
        HttpStatus status;
        if(e.contains("banned")) {
            status = HttpStatus.LOCKED;
        } else {
            status = HttpStatus.UNAUTHORIZED;
        }
        response.setStatus(status.value());

        // Create the error response JSON object
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .errorMessage(e)
                .errorCode(String.valueOf(status.value()))
                .build();

        // Convert the error response to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);

        // Set the response content type to application/json
        response.setContentType("application/json");

        // Write the JSON error response to the response body
        response.getWriter().write(jsonErrorResponse);
        response.getWriter().flush();
    }
}
