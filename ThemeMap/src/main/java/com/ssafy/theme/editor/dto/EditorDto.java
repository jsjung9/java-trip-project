package com.ssafy.theme.editor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorDto {
    private String editorId;
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9_]{4,30}$")
    private String id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 8, max = 72)
    private String pw;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;
    private String emailId;
    private String emailDomain;
    @NotBlank
    @Size(max = 30)
    private String editorName;
    private String likeSum;
    private String joinDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String token;

}
