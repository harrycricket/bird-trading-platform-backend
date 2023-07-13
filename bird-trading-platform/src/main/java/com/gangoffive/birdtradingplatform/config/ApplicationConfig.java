package com.gangoffive.birdtradingplatform.config;

import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.enums.ShopOwnerStatus;
import com.gangoffive.birdtradingplatform.exception.AuthenticateException;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfig {
    private final AccountRepository accountRepository;

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                var tmp = accountRepository.findByEmail(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("Not found this email"));
                log.info("tmp.getStatus() {}", tmp.getStatus());
                if (tmp.getShopOwner() != null) {
                    if (tmp.getStatus().equals(AccountStatus.BANNED)) {
                        throw new AuthenticateException("Email user ban");
                    } else if (tmp.getShopOwner().getStatus().equals(ShopOwnerStatus.BAN)) {
                        throw new AuthenticateException("Email shop ban");
                    } else {
                        return UserPrincipal.create(tmp);
                    }
                } else if (tmp.getStatus().equals(AccountStatus.BANNED)) {
                    throw new AuthenticateException("Email user ban");
                } else {
                    return UserPrincipal.create(tmp);
                }
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
