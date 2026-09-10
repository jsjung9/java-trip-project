package com.ssafy.theme.theme.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.theme.common.ForbiddenException;
import com.ssafy.theme.common.NotFoundException;
import com.ssafy.theme.editor.dto.EditorDto;
import com.ssafy.theme.editor.service.EditorIdentity;
import com.ssafy.theme.theme.dto.TagDto;
import com.ssafy.theme.theme.dto.ThemeDto;
import com.ssafy.theme.theme.mapper.ThemeMapper;

@Service
public class ThemeServiceImpl implements ThemeService {
    private final ThemeMapper themeMapper;
    private final EditorIdentity editorIdentity;

    public ThemeServiceImpl(ThemeMapper themeMapper, EditorIdentity editorIdentity) {
        this.themeMapper = themeMapper;
        this.editorIdentity = editorIdentity;
    }

    @Override
    @Transactional
    public String createTheme(ThemeDto theme, String loginId) {
        theme.setEditorId(editorIdentity.require(loginId).getEditorId());
        theme.setLikeSum("0");
        themeMapper.createTheme(theme);
        return theme.getThemeId();
    }

    @Override public List<ThemeDto> hotTheme() { return themeMapper.hotTheme(); }
    @Override public List<ThemeDto> themesOfPlace(String placeId) { return themeMapper.themesOfPlace(placeId); }
    @Override
    public List<ThemeDto> themesOfEditor(String editorId, String loginId) {
        assertCurrentEditor(editorId, loginId);
        return themeMapper.themesOfEditor(editorId);
    }
    @Override public List<ThemeDto> visibleThemesOfEditor(String editorId) { return themeMapper.visibleThemesOfEditor(editorId); }
    @Override
    public List<ThemeDto> themesOfLike(String editorId, String loginId) {
        assertCurrentEditor(editorId, loginId);
        return themeMapper.themesOfLike(editorId);
    }

    @Override
    @Transactional
    public void updateTheme(ThemeDto theme, String loginId) {
        theme.setEditorId(editorIdentity.require(loginId).getEditorId());
        assertOwner(theme.getThemeId(), theme.getEditorId());
        if (themeMapper.updateTheme(theme) != 1) {
            throw new NotFoundException("테마를 찾을 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteTheme(String themeId, String loginId) {
        String editorId = editorIdentity.require(loginId).getEditorId();
        assertOwner(themeId, editorId);
        if (themeMapper.deleteTheme(themeId, editorId) != 1) {
            throw new NotFoundException("테마를 찾을 수 없습니다.");
        }
    }

    @Override
    public List<ThemeDto> themesOfTag(List<TagDto> tags) {
        if (tags == null || tags.isEmpty()) {
            return allThemes();
        }
        return themeMapper.themesOfTag(tags, tags.size());
    }

    @Override public List<ThemeDto> allThemes() { return themeMapper.allThemes(); }
    @Override public List<TagDto> allTags() { return themeMapper.allTags(); }
    @Override
    public ThemeDto getTheme(String themeId, String loginId) {
        ThemeDto theme = requireTheme(themeId);
        if (!"1".equals(theme.getVisible())) {
            if (loginId == null || !Objects.equals(theme.getEditorId(), editorIdentity.numericId(loginId))) {
                throw new ForbiddenException("비공개 테마는 작성자만 볼 수 있습니다.");
            }
        }
        return theme;
    }

    @Override
    public boolean didLike(String loginId, String themeId) {
        return themeMapper.didLike(editorIdentity.require(loginId).getEditorId(), themeId) > 0;
    }

    @Override
    @Transactional
    public void setLike(String loginId, String themeId, boolean liked) {
        EditorDto current = editorIdentity.require(loginId);
        ThemeDto theme = requireTheme(themeId);
        int changed = liked
                ? themeMapper.postLike(current.getEditorId(), themeId)
                : themeMapper.disLike(current.getEditorId(), themeId);
        if (changed == 0) {
            return;
        }
        if (liked) {
            themeMapper.increaseThemeLike(themeId);
            themeMapper.increaseEditorLike(theme.getEditorId());
        } else {
            themeMapper.decreaseThemeLike(themeId);
            themeMapper.decreaseEditorLike(theme.getEditorId());
        }
    }

    @Override public List<TagDto> tagsOfTheme(String themeId) { return themeMapper.tagsOfTheme(themeId); }

    @Override
    @Transactional
    public void updateTags(String themeId, List<TagDto> tags, String loginId) {
        assertOwner(themeId, editorIdentity.require(loginId).getEditorId());
        themeMapper.deleteTags(themeId);
        if (tags != null) {
            tags.stream().filter(Objects::nonNull).map(TagDto::getTagId).filter(Objects::nonNull)
                    .distinct().forEach(tagId -> themeMapper.insertTags(themeId, tagId));
        }
    }

    private void assertCurrentEditor(String editorId, String loginId) {
        if (!Objects.equals(editorId, editorIdentity.numericId(loginId))) {
            throw new ForbiddenException("본인의 테마 정보만 조회할 수 있습니다.");
        }
    }

    private ThemeDto requireTheme(String themeId) {
        ThemeDto theme = themeMapper.getTheme(themeId);
        if (theme == null) throw new NotFoundException("테마를 찾을 수 없습니다.");
        return theme;
    }

    private void assertOwner(String themeId, String editorId) {
        ThemeDto theme = requireTheme(themeId);
        if (!Objects.equals(theme.getEditorId(), editorId)) {
            throw new ForbiddenException("테마 작성자만 변경할 수 있습니다.");
        }
    }
}
