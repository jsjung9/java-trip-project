package com.ssafy.theme.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;

class JWTUtilTest {
    private final JWTUtil jwt = new JWTUtil(
            "test-jwt-secret-key-that-is-at-least-32-bytes", 60_000, 120_000);

    @Test
    void distinguishesAccessAndRefreshTokens() {
        String access = jwt.createAccessToken("editor");
        String refresh = jwt.createRefreshToken("editor");

        assertThat(jwt.getAccessTokenSubject(access)).isEqualTo("editor");
        assertThat(jwt.getRefreshTokenSubject(refresh)).isEqualTo("editor");
        assertThatThrownBy(() -> jwt.getAccessTokenSubject(refresh)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsTamperedTokens() {
        String token = jwt.createAccessToken("editor") + "changed";
        assertThatThrownBy(() -> jwt.getAccessTokenSubject(token)).isInstanceOf(JwtException.class);
    }
}
