package com.ssafy.theme.theme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ssafy.theme.common.ForbiddenException;
import com.ssafy.theme.editor.dto.EditorDto;
import com.ssafy.theme.editor.service.EditorIdentity;
import com.ssafy.theme.theme.dto.ThemeDto;
import com.ssafy.theme.theme.mapper.ThemeMapper;

class ThemeServiceImplTest {
    private final ThemeMapper mapper = mock(ThemeMapper.class);
    private final EditorIdentity identity = mock(EditorIdentity.class);
    private ThemeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ThemeServiceImpl(mapper, identity);
        EditorDto current = new EditorDto();
        current.setEditorId("7");
        when(identity.require("login-id")).thenReturn(current);
    }

    @Test
    void returnsIdAssignedByGeneratedKey() {
        ThemeDto theme = theme("", "7");
        when(mapper.createTheme(theme)).thenAnswer(invocation -> {
            theme.setThemeId("42");
            return 1;
        });

        assertThat(service.createTheme(theme, "login-id")).isEqualTo("42");
    }

    @Test
    void rejectsUpdateByAnotherEditor() {
        ThemeDto stored = theme("42", "8");
        when(mapper.getTheme("42")).thenReturn(stored);

        assertThatThrownBy(() -> service.updateTheme(theme("42", "7"), "login-id"))
                .isInstanceOf(ForbiddenException.class);
        verify(mapper, never()).updateTheme(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void incrementsCountersOnlyWhenLikeRelationWasInserted() {
        ThemeDto stored = theme("42", "8");
        when(mapper.getTheme("42")).thenReturn(stored);
        when(mapper.postLike("7", "42")).thenReturn(0);

        service.setLike("login-id", "42", true);

        verify(mapper, never()).increaseThemeLike("42");
        verify(mapper, never()).increaseEditorLike("8");
    }

    @Test
    void hidesPrivateThemeFromAnonymousViewer() {
        ThemeDto stored = theme("42", "8");
        stored.setVisible("0");
        when(mapper.getTheme("42")).thenReturn(stored);

        assertThatThrownBy(() -> service.getTheme("42", null))
                .isInstanceOf(ForbiddenException.class);
    }

    private ThemeDto theme(String id, String editorId) {
        return new ThemeDto(id, "서울 산책", "설명", editorId, "walk", "1", "0");
    }
}
