package com.ssafy.theme.place.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ssafy.theme.common.ConflictException;
import com.ssafy.theme.common.ForbiddenException;
import com.ssafy.theme.editor.service.EditorIdentity;
import com.ssafy.theme.place.dto.LinkDto;
import com.ssafy.theme.place.mapper.PlaceMapper;
import com.ssafy.theme.theme.dto.ThemeDto;
import com.ssafy.theme.theme.mapper.ThemeMapper;

class PlaceServiceImplTest {
    private final PlaceMapper placeMapper = mock(PlaceMapper.class);
    private final ThemeMapper themeMapper = mock(ThemeMapper.class);
    private final EditorIdentity identity = mock(EditorIdentity.class);
    private PlaceServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PlaceServiceImpl(placeMapper, themeMapper, identity);
        when(identity.numericId("login-id")).thenReturn("7");
    }

    @Test
    void contributorCannotAddSecondPlace() {
        LinkDto link = new LinkDto("10", "place-2", null);
        when(themeMapper.getTheme("10")).thenReturn(theme("10", "8"));
        when(themeMapper.findEditor("10")).thenReturn("8");
        when(placeMapper.isThere("place-2")).thenReturn(1);
        when(placeMapper.getSpareNum("10", "7")).thenReturn(1);

        assertThatThrownBy(() -> service.linkPlace(link, "login-id"))
                .isInstanceOf(ConflictException.class);
        verify(placeMapper, never()).linkPlace(link);
    }

    @Test
    void rejectsOutOfRangeScoreBeforeWriting() {
        assertThatThrownBy(() -> service.keepScore("place-1", "6", "login-id"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(placeMapper, never()).upsertScore("place-1", "7", 6);
    }

    @Test
    void privateThemeRejectsOtherContributors() {
        LinkDto link = new LinkDto("10", "place-2", null);
        ThemeDto privateTheme = theme("10", "8");
        privateTheme.setType("0");
        when(themeMapper.getTheme("10")).thenReturn(privateTheme);
        when(themeMapper.findEditor("10")).thenReturn("8");
        when(placeMapper.isThere("place-2")).thenReturn(1);

        assertThatThrownBy(() -> service.linkPlace(link, "login-id"))
                .isInstanceOf(ForbiddenException.class);
        verify(placeMapper, never()).linkPlace(link);
    }

    private ThemeDto theme(String id, String editorId) {
        return new ThemeDto(id, "테마", "설명", editorId, "type", "1", "0");
    }
}
