package com.ssafy.theme.place.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.theme.common.ConflictException;
import com.ssafy.theme.common.ForbiddenException;
import com.ssafy.theme.common.NotFoundException;
import com.ssafy.theme.editor.service.EditorIdentity;
import com.ssafy.theme.place.dto.LinkDto;
import com.ssafy.theme.place.dto.PlaceDto;
import com.ssafy.theme.place.mapper.PlaceMapper;
import com.ssafy.theme.theme.mapper.ThemeMapper;

@Service
public class PlaceServiceImpl implements PlaceService {
    private static final int OWNER_PLACE_LIMIT = 10;
    private static final int CONTRIBUTOR_PLACE_LIMIT = 1;

    private final PlaceMapper placeMapper;
    private final ThemeMapper themeMapper;
    private final EditorIdentity editorIdentity;

    public PlaceServiceImpl(PlaceMapper placeMapper, ThemeMapper themeMapper, EditorIdentity editorIdentity) {
        this.placeMapper = placeMapper;
        this.themeMapper = themeMapper;
        this.editorIdentity = editorIdentity;
    }

    @Override
    public void createPlace(PlaceDto place) {
        validateCoordinate(place.getLatitude(), -90, 90, "위도");
        validateCoordinate(place.getLongitude(), -180, 180, "경도");
        placeMapper.createPlace(place);
    }

    @Override public List<PlaceDto> hotPlace() { return placeMapper.hotPlace(); }
    @Override public List<PlaceDto> placesOfTheme(String themeId) { return placeMapper.placesOfTheme(themeId); }

    @Override
    @Transactional
    public void linkPlace(LinkDto link, String loginId) {
        String editorId = editorIdentity.numericId(loginId);
        if (themeMapper.getTheme(link.getThemeId()) == null) {
            throw new NotFoundException("테마를 찾을 수 없습니다.");
        }
        if (placeMapper.isThere(link.getPlaceId()) == 0) {
            throw new NotFoundException("장소를 먼저 등록해 주세요.");
        }
        if (placeMapper.isInTheme(link.getThemeId(), link.getPlaceId()) > 0) {
            return;
        }
        boolean owner = Objects.equals(themeMapper.findEditor(link.getThemeId()), editorId);
        if ("0".equals(themeMapper.getTheme(link.getThemeId()).getType()) && !owner) {
            throw new ForbiddenException("비공개 테마에는 작성자만 장소를 추가할 수 있습니다.");
        }
        int limit = owner ? OWNER_PLACE_LIMIT : CONTRIBUTOR_PLACE_LIMIT;
        if (placeMapper.getSpareNum(link.getThemeId(), editorId) >= limit) {
            throw new ConflictException("이 테마에 추가할 수 있는 장소 수를 초과했습니다.");
        }
        link.setEditorId(editorId);
        placeMapper.linkPlace(link);
    }

    @Override
    @Transactional
    public void keepScore(String placeId, String rawScore, String loginId) {
        int score;
        try {
            score = Integer.parseInt(rawScore);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("평점은 1부터 5 사이의 정수여야 합니다.");
        }
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("평점은 1부터 5 사이의 정수여야 합니다.");
        }
        if (placeMapper.isThere(placeId) == 0) {
            throw new NotFoundException("장소를 찾을 수 없습니다.");
        }
        placeMapper.upsertScore(placeId, editorIdentity.numericId(loginId), score);
        placeMapper.recalculateScore(placeId);
    }

    @Override public boolean isThere(String placeId) { return placeMapper.isThere(placeId) > 0; }
    @Override public boolean isInTheme(String themeId, String placeId) { return placeMapper.isInTheme(themeId, placeId) > 0; }

    @Override
    @Transactional
    public void deletePlace(String themeId, String placeId, String loginId) {
        String editorId = editorIdentity.numericId(loginId);
        if (placeMapper.deletePlace(themeId, placeId, editorId) == 0) {
            throw new ForbiddenException("테마 작성자 또는 장소를 추가한 사용자만 삭제할 수 있습니다.");
        }
    }

    @Override public String whoCreated(String themeId, String placeId) { return placeMapper.whoCreated(themeId, placeId); }

    @Override
    public int getSpareNum(String themeId, String loginId) {
        return placeMapper.getSpareNum(themeId, editorIdentity.numericId(loginId));
    }

    private void validateCoordinate(String rawValue, int minimum, int maximum, String field) {
        try {
            BigDecimal value = new BigDecimal(rawValue);
            if (value.compareTo(BigDecimal.valueOf(minimum)) < 0
                    || value.compareTo(BigDecimal.valueOf(maximum)) > 0) {
                throw new IllegalArgumentException(field + " 범위를 확인해 주세요.");
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(field + " 형식을 확인해 주세요.");
        }
    }
}
