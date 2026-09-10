package com.ssafy.theme.util;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ssafy.theme.editor.dto.EditorDto;
import com.ssafy.theme.editor.mapper.EditorMapper;

@Component
public class PasswordService {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Encrypt legacyEncrypt;
    private final EditorMapper editorMapper;

    public PasswordService(Encrypt legacyEncrypt, EditorMapper editorMapper) {
        this.legacyEncrypt = legacyEncrypt;
        this.editorMapper = editorMapper;
    }

    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matchesAndUpgrade(String rawPassword, EditorDto editor) {
        String storedPassword = editor.getPw();
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2")) {
            return encoder.matches(rawPassword, storedPassword);
        }
        if (editor.getSalt() == null
                || !storedPassword.equals(legacyEncrypt.getEncrypt(rawPassword, editor.getSalt()))) {
            return false;
        }
        String upgraded = encode(rawPassword);
        editorMapper.updateCredentials(Map.of("id", editor.getId(), "pw", upgraded));
        editor.setPw(upgraded);
        editor.setSalt(null);
        return true;
    }
}
