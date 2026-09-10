package com.ssafy.theme.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey key;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expiretime}") long accessTokenExpireTime,
            @Value("${jwt.refresh-token.expiretime}") long refreshTokenExpireTime) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT_SECRET_KEY must contain at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public String createAccessToken(String id) {
        return create(id, ACCESS, accessTokenExpireTime);
    }

    public String createRefreshToken(String id) {
        return create(id, REFRESH, refreshTokenExpireTime);
    }

    public String getAccessTokenSubject(String token) {
        return subject(token, ACCESS);
    }

    public String getRefreshTokenSubject(String token) {
        return subject(token, REFRESH);
    }

    public long getRefreshTokenMaxAgeSeconds() {
        return refreshTokenExpireTime / 1000;
    }

    private String create(String id, String type, long expireTime) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(id)
                .claim(TOKEN_TYPE, type)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(expireTime)))
                .signWith(key)
                .compact();
    }

    private String subject(String token, String expectedType) {
        if (token == null || token.isBlank()) {
            throw new JwtException("Token is missing");
        }
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        if (!expectedType.equals(claims.get(TOKEN_TYPE, String.class))) {
            throw new JwtException("Unexpected token type");
        }
        return claims.getSubject();
    }
}
