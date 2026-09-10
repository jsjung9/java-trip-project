package com.ssafy.theme.theme.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.theme.theme.dto.TagDto;
import com.ssafy.theme.theme.dto.ThemeDto;

@Mapper
public interface ThemeMapper {
    int createTheme(ThemeDto theme);
    List<ThemeDto> hotTheme();
    List<ThemeDto> themesOfPlace(String placeId);
    List<ThemeDto> themesOfEditor(String editorId);
    List<ThemeDto> visibleThemesOfEditor(String editorId);
    List<ThemeDto> themesOfLike(String editorId);
    int updateTheme(ThemeDto theme);
    int deleteTheme(@Param("themeId") String themeId, @Param("editorId") String editorId);
    List<ThemeDto> themesOfTag(@Param("tags") List<TagDto> tags, @Param("tagCount") int tagCount);
    List<ThemeDto> allThemes();
    List<TagDto> allTags();
    ThemeDto getTheme(String themeId);
    int didLike(@Param("editorId") String editorId, @Param("themeId") String themeId);
    int postLike(@Param("editorId") String editorId, @Param("themeId") String themeId);
    int increaseThemeLike(String themeId);
    int increaseEditorLike(String editorId);
    int disLike(@Param("editorId") String editorId, @Param("themeId") String themeId);
    int decreaseThemeLike(String themeId);
    int decreaseEditorLike(String editorId);
    String findEditor(String themeId);
    List<TagDto> tagsOfTheme(String themeId);
    int deleteTags(String themeId);
    int insertTags(@Param("themeId") String themeId, @Param("tagId") String tagId);
}
