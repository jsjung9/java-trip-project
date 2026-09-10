package com.ssafy.theme.place.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {

    @NotBlank
    @Size(max = 64)
    private String placeId;
    @NotBlank
    @Size(max = 150)
    private String placeName;
    @NotBlank
    private String latitude;
    @NotBlank
    private String longitude;
    private String scoreSum;
    private String scoreCount;
    @Size(max = 255)
    private String address;
    @Size(max = 30)
    private String phone;

}
