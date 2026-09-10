package com.ssafy.theme.place.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto {

    @NotBlank
    private String themeId;
    @NotBlank
    private String placeId;
    private String editorId;

}
