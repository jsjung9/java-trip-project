package com.ssafy.theme.place.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto {

    @NotBlank
    private String placeId;
    @NotBlank
    @Pattern(regexp = "^[1-5]$")
    private String score;

}
