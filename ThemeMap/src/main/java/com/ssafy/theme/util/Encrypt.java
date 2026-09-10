package com.ssafy.theme.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

/** Supports verification of passwords created by the original 2023 application. */
@Component
public class Encrypt {
    public String getEncrypt(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(encoded.length * 2);
            for (byte value : encoded) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
