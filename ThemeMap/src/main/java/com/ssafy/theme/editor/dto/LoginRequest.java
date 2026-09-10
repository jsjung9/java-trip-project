package com.ssafy.theme.editor.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String id, @NotBlank String pw) {
    public EditorDto toEditorDto() {
        EditorDto editor = new EditorDto();
        editor.setId(id);
        editor.setPw(pw);
        return editor;
    }
}
