package com.ssafy.theme.theme.controller;

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

import com.ssafy.theme.theme.dto.LikeDto;
import com.ssafy.theme.theme.dto.TagDto;
import com.ssafy.theme.theme.dto.TagListDto;
import com.ssafy.theme.theme.dto.ThemeDto;
import com.ssafy.theme.theme.service.ThemeService;

@RestController
@RequestMapping("/theme")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTheme(Authentication authentication, @RequestBody ThemeDto theme) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(themeService.createTheme(theme, authentication.getName()));
    }

    @GetMapping("/hot")
    public List<ThemeDto> hotTheme() { return themeService.hotTheme(); }

    @GetMapping("/place/{placeId}")
    public List<ThemeDto> themesOfPlace(@PathVariable String placeId) {
        return themeService.themesOfPlace(placeId);
    }

    @GetMapping("/editor/{editorId}")
    public List<ThemeDto> themesOfEditor(@PathVariable String editorId) {
        return themeService.themesOfEditor(editorId);
    }

    @GetMapping("/visible/{editorId}")
    public List<ThemeDto> visibleThemesOfEditor(@PathVariable String editorId) {
        return themeService.visibleThemesOfEditor(editorId);
    }

    @GetMapping("/like/{editorId}")
    public List<ThemeDto> themesOfLike(@PathVariable String editorId) {
        return themeService.themesOfLike(editorId);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateTheme(Authentication authentication, @RequestBody ThemeDto theme) {
        themeService.updateTheme(theme, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{themeId}")
    public ResponseEntity<Void> deleteTheme(Authentication authentication, @PathVariable String themeId) {
        themeService.deleteTheme(themeId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tag")
    public List<ThemeDto> themesOfTag(@RequestBody TagListDto tagList) {
        return themeService.themesOfTag(tagList.getTags());
    }

    @GetMapping("/all")
    public List<ThemeDto> allThemes() { return themeService.allThemes(); }

    @GetMapping("/allTags")
    public List<TagDto> allTags() { return themeService.allTags(); }

    @GetMapping("/get/{themeId}")
    public ThemeDto getTheme(@PathVariable String themeId) { return themeService.getTheme(themeId); }

    @GetMapping("/didLike/{ignoredEditorId}/{themeId}")
    public boolean didLike(Authentication authentication, @PathVariable String ignoredEditorId,
            @PathVariable String themeId) {
        return themeService.didLike(authentication.getName(), themeId);
    }

    @PostMapping("/postLike")
    public ResponseEntity<Void> postLike(Authentication authentication, @RequestBody LikeDto like) {
        themeService.setLike(authentication.getName(), like.getThemeId(), true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/disLike")
    public ResponseEntity<Void> disLike(Authentication authentication, @RequestBody LikeDto like) {
        themeService.setLike(authentication.getName(), like.getThemeId(), false);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tagsOfTheme/{themeId}")
    public List<TagDto> tagsOfTheme(@PathVariable String themeId) {
        return themeService.tagsOfTheme(themeId);
    }

    @PostMapping("/updateTag/{themeId}")
    public ResponseEntity<Void> updateTag(Authentication authentication, @RequestBody TagListDto tagList,
            @PathVariable String themeId) {
        themeService.updateTags(themeId, tagList.getTags(), authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
