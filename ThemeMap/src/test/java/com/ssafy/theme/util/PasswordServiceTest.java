package com.ssafy.theme.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.ssafy.theme.editor.dto.EditorDto;
import com.ssafy.theme.editor.mapper.EditorMapper;

class PasswordServiceTest {
    private final EditorMapper mapper = mock(EditorMapper.class);
    private final Encrypt legacyEncrypt = new Encrypt();
    private final PasswordService passwords = new PasswordService(legacyEncrypt, mapper);

    @Test
    void encodesNewPasswordsWithBcrypt() {
        String encoded = passwords.encode("correct horse battery staple");
        EditorDto editor = editor("user", encoded, null);
        assertThat(passwords.matchesAndUpgrade("correct horse battery staple", editor)).isTrue();
    }

    @Test
    void upgradesLegacyHashAfterSuccessfulLogin() {
        String salt = "legacy-salt";
        EditorDto editor = editor("user", legacyEncrypt.getEncrypt("password123", salt), salt);

        assertThat(passwords.matchesAndUpgrade("password123", editor)).isTrue();
        assertThat(editor.getPw()).startsWith("$2");
        assertThat(editor.getSalt()).isNull();
        verify(mapper).updateCredentials(argThat(values -> "user".equals(values.get("id"))
                && ((String) values.get("pw")).startsWith("$2")));
    }

    @Test
    void doesNotUpgradeWrongLegacyPassword() {
        EditorDto editor = editor("user", legacyEncrypt.getEncrypt("right-password", "salt"), "salt");
        assertThat(passwords.matchesAndUpgrade("wrong-password", editor)).isFalse();
    }

    private EditorDto editor(String id, String password, String salt) {
        EditorDto editor = new EditorDto();
        editor.setId(id);
        editor.setPw(password);
        editor.setSalt(salt);
        return editor;
    }
}
