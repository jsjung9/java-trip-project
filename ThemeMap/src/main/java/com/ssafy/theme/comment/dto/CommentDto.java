package com.ssafy.theme.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String commentId;
    @NotBlank
    private String placeId;
    @NotBlank
    @Size(max = 500)
    private String content;
}
