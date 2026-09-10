package com.ssafy.theme.editor.service;

import org.springframework.stereotype.Component;

import com.ssafy.theme.common.NotFoundException;
import com.ssafy.theme.editor.dto.EditorDto;
import com.ssafy.theme.editor.mapper.EditorMapper;

@Component
public class EditorIdentity {
    private final EditorMapper editorMapper;

    public EditorIdentity(EditorMapper editorMapper) {
        this.editorMapper = editorMapper;
    }

    public EditorDto require(String loginId) {
        try {
            EditorDto editor = editorMapper.editorInfo(loginId);
            if (editor == null) throw new NotFoundException("사용자를 찾을 수 없습니다.");
            return editor;
        } catch (NotFoundException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("사용자 정보를 조회하지 못했습니다.", exception);
        }
    }

    public String numericId(String loginId) {
        return require(loginId).getEditorId();
    }
}
