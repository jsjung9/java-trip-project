package com.ssafy.theme.theme.service;

import java.util.List;

import com.ssafy.theme.theme.dto.TagDto;
import com.ssafy.theme.theme.dto.ThemeDto;

public interface ThemeService {
    String createTheme(ThemeDto theme, String loginId);
    List<ThemeDto> hotTheme();
    List<ThemeDto> themesOfPlace(String placeId);
    List<ThemeDto> themesOfEditor(String editorId, String loginId);
    List<ThemeDto> visibleThemesOfEditor(String editorId);
    List<ThemeDto> themesOfLike(String editorId, String loginId);
    void updateTheme(ThemeDto theme, String loginId);
    void deleteTheme(String themeId, String loginId);
    List<ThemeDto> themesOfTag(List<TagDto> tags);
    List<ThemeDto> allThemes();
    List<TagDto> allTags();
    ThemeDto getTheme(String themeId, String loginId);
    boolean didLike(String loginId, String themeId);
    void setLike(String loginId, String themeId, boolean liked);
    List<TagDto> tagsOfTheme(String themeId);
    void updateTags(String themeId, List<TagDto> tags, String loginId);
}
