package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final AppProperties appProperties;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractAudience(String token) {
        return extractClaim(token, Claims::getAudience);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(UserDetails userDetails, String ...staffAccount) {
        return generateToken(Map.of("staffAccount", staffAccount), userDetails);
    }

    public String generateToken(UserDetails userDetails, List<String> scopes) {
        return generateToken(Map.of("scopes", scopes), userDetails);
    }


    public String generateToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails
    ) {
        return generateToken(extractClaims, userDetails, appProperties.getAuth().getTokenExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, appProperties.getAuth().getRefreshTokenExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails, String ...scopes) {
        return generateToken(Map.of("scopes", scopes), userDetails, appProperties.getAuth().getRefreshTokenExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails, List<String> scopes) {
        return generateToken(Map.of("scopes", scopes), userDetails, appProperties.getAuth().getRefreshTokenExpiration());
    }

    private String generateToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails,
            Long expiration
    ) {
        if (extractClaims.containsKey("staffAccount")) {
            return Jwts
                    .builder()
                    .setClaims(extractClaims)
                    .setAudience(((String[]) extractClaims.get("staffAccount"))[0])
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(appProperties.getAuth().getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
