package com.gangoffive.birdtradingplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();

    private final OAuth2 oauth2 = new OAuth2();
    private final Cors cors = new Cors();
    public static class Auth {
        private String secretKey;
        private Long tokenExpiration;
        private Long refreshTokenExpiration;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public Long getTokenExpiration() {
            return tokenExpiration;
        }

        public void setTokenExpiration(Long tokenExpiration) {
            this.tokenExpiration = tokenExpiration;
        }

        public Long getRefreshTokenExpiration() {
            return refreshTokenExpiration;
        }

        public void setRefreshTokenExpiration(Long refreshTokenExpiration) {
            this.refreshTokenExpiration = refreshTokenExpiration;
        }
    }

    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }
        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }

    public static final class Cors {
        private String allowedOrigins;

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public Auth getAuth() {
        return auth;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }

    public Cors getCors() {
        return cors;
    }
}
