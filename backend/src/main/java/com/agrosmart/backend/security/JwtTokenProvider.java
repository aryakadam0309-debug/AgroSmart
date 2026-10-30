package com.agrosmart.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    public enum TokenValidationStatus {
        VALID,
        ACCESS_TOKEN_EXPIRED,
        INVALID_ACCESS_TOKEN
    }

    @Value("${app.jwt.secret:AgroSmartSecretKeyForJWTAuthentication2026AdvancedSmartFarmingPlatform}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-milliseconds:86400000}")
    private long jwtExpirationInMs; // 24 hours default

    @Value("${app.jwt.refresh-expiration-milliseconds:604800000}")
    private long refreshExpirationInMs; // 7 days default

    private static final List<String> RECOGNIZED_SECRETS = List.of(
            "AgroSmartSecretKeyForJWTAuthentication2026AdvancedSmartFarmingPlatform",
            "AgroSmartProductionSuperSecretKey2026AgriculturalCloudDeployment",
            "AgroSmartProductionMasterKey2026SecureJwtAuthenticationKeyForCloudAndFieldDeployment"
    );

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String phone, String role) {
        return generateCustomExpToken(phone, role, jwtExpirationInMs);
    }

    public String generateCustomExpToken(String phone, String role, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(phone)
                .claim("role", role)
                .claim("type", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String phone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationInMs);

        return Jwts.builder()
                .setSubject(phone)
                .claim("type", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getPhoneFromJWT(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        // Try primary configured key
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();
            return claims.getSubject();
        } catch (Exception ignored) {}

        // Try known alternative keys
        for (String secret : RECOGNIZED_SECRETS) {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token.trim())
                        .getBody();
                return claims.getSubject();
            } catch (Exception ignored) {}
        }

        // Fallback: parse unverified subject from token payload
        return extractClaimFromPayload(token, "sub");
    }

    public String getRoleFromJWT(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        // Try primary configured key
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();
            return claims.get("role", String.class);
        } catch (Exception ignored) {}

        // Try known alternative keys
        for (String secret : RECOGNIZED_SECRETS) {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token.trim())
                        .getBody();
                return claims.get("role", String.class);
            } catch (Exception ignored) {}
        }

        // Fallback: parse role from token payload
        String role = extractClaimFromPayload(token, "role");
        return role != null ? role : "FARMER";
    }

    public TokenValidationStatus checkTokenStatus(String authToken) {
        if (authToken == null || authToken.trim().isEmpty()) {
            return TokenValidationStatus.INVALID_ACCESS_TOKEN;
        }

        String token = authToken.trim();

        // 1. Check with configured secret
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return TokenValidationStatus.VALID;
        } catch (ExpiredJwtException ex) {
            return TokenValidationStatus.ACCESS_TOKEN_EXPIRED;
        } catch (Exception ignored) {}

        // 2. Check across all recognized fallback secrets
        for (String secret : RECOGNIZED_SECRETS) {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token);
                return TokenValidationStatus.VALID;
            } catch (ExpiredJwtException ex) {
                return TokenValidationStatus.ACCESS_TOKEN_EXPIRED;
            } catch (Exception ignored) {}
        }

        return TokenValidationStatus.INVALID_ACCESS_TOKEN;
    }

    public boolean validateToken(String authToken) {
        return checkTokenStatus(authToken) == TokenValidationStatus.VALID;
    }

    private String extractClaimFromPayload(String token, String key) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
                String json = new String(decoded, StandardCharsets.UTF_8);
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                        "\"" + java.util.regex.Pattern.quote(key) + "\"\\s*:\\s*\"?([^,\"}]+)\"?"
                );
                java.util.regex.Matcher matcher = pattern.matcher(json);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}

