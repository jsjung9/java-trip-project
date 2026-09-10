package com.ssafy.theme.place.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.theme.place.dto.LinkDto;
import com.ssafy.theme.place.dto.PlaceDto;
import com.ssafy.theme.place.dto.ScoreDto;
import com.ssafy.theme.place.service.PlaceService;

@RestController
@RequestMapping("/place")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createPlace(@RequestBody PlaceDto place) {
        placeService.createPlace(place);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/hot")
    public List<PlaceDto> hotPlace() { return placeService.hotPlace(); }

    @GetMapping("/theme/{themeId}")
    public List<PlaceDto> placesOfTheme(@PathVariable String themeId) {
        return placeService.placesOfTheme(themeId);
    }

    @PutMapping("/score")
    public ResponseEntity<Void> keepScore(Authentication authentication, @RequestBody ScoreDto score) {
        placeService.keepScore(score.getPlaceId(), score.getScore(), authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/link")
    public ResponseEntity<Void> linkPlace(Authentication authentication, @RequestBody LinkDto link) {
        placeService.linkPlace(link, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/isThere/{placeId}")
    public boolean isThere(@PathVariable String placeId) { return placeService.isThere(placeId); }

    @GetMapping("/isInTheme/{themeId}/{placeId}")
    public boolean isInTheme(@PathVariable String themeId, @PathVariable String placeId) {
        return placeService.isInTheme(themeId, placeId);
    }

    @DeleteMapping("/delete/{themeId}/{placeId}")
    public ResponseEntity<Void> deletePlace(Authentication authentication, @PathVariable String themeId,
            @PathVariable String placeId) {
        placeService.deletePlace(themeId, placeId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/who/{themeId}/{placeId}")
    public String whoCreated(@PathVariable String themeId, @PathVariable String placeId) {
        return placeService.whoCreated(themeId, placeId);
    }

    @GetMapping("/spare/{themeId}/{ignoredEditorId}")
    public int getSpareNum(Authentication authentication, @PathVariable String themeId,
            @PathVariable String ignoredEditorId) {
        return placeService.getSpareNum(themeId, authentication.getName());
    }
}
