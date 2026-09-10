package com.ssafy.theme.theme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDto {

    private String themeId;
    @NotBlank
    @Size(max = 100)
    private String themeName;
    @Size(max = 1000)
    private String description;
    private String editorId;
    @NotBlank
    @Pattern(regexp = "^[01]$")
    private String type;
    @NotBlank
    @Pattern(regexp = "^[01]$")
    private String visible;
    private String likeSum;

}
