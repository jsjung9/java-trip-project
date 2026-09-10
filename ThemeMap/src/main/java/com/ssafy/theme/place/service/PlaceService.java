package com.ssafy.theme.place.service;

import java.util.List;

import com.ssafy.theme.place.dto.LinkDto;
import com.ssafy.theme.place.dto.PlaceDto;

public interface PlaceService {
    void createPlace(PlaceDto place);
    List<PlaceDto> hotPlace();
    List<PlaceDto> placesOfTheme(String themeId);
    void linkPlace(LinkDto link, String loginId);
    void keepScore(String placeId, String score, String loginId);
    boolean isThere(String placeId);
    boolean isInTheme(String themeId, String placeId);
    void deletePlace(String themeId, String placeId, String loginId);
    String whoCreated(String themeId, String placeId);
    int getSpareNum(String themeId, String loginId);
}
