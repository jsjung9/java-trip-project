package com.ssafy.theme.place.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.theme.place.dto.LinkDto;
import com.ssafy.theme.place.dto.PlaceDto;

@Mapper
public interface PlaceMapper {
    int createPlace(PlaceDto place);
    List<PlaceDto> hotPlace();
    List<PlaceDto> placesOfTheme(String themeId);
    int linkPlace(LinkDto link);
    int upsertScore(@Param("placeId") String placeId, @Param("editorId") String editorId,
            @Param("score") int score);
    int recalculateScore(String placeId);
    int isThere(String placeId);
    int isInTheme(@Param("themeId") String themeId, @Param("placeId") String placeId);
    int deletePlace(@Param("themeId") String themeId, @Param("placeId") String placeId,
            @Param("editorId") String editorId);
    String whoCreated(@Param("themeId") String themeId, @Param("placeId") String placeId);
    int getSpareNum(@Param("themeId") String themeId, @Param("editorId") String editorId);
}
