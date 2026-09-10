package com.ssafy.theme.editor.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.theme.editor.dto.EditorDto;
import com.ssafy.theme.editor.mapper.EditorMapper;
import com.ssafy.theme.util.PasswordService;

@Service
public class EditorServiceImpl implements EditorService {
    private final EditorMapper editorMapper;
    private final PasswordService passwordService;

    public EditorServiceImpl(EditorMapper editorMapper, PasswordService passwordService) {
        this.editorMapper = editorMapper;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public EditorDto login(EditorDto credentials) throws Exception {
        EditorDto editor = editorMapper.login(credentials);
        if (editor == null || !passwordService.matchesAndUpgrade(credentials.getPw(), editor)) {
            return null;
        }
        return editor;
    }

    @Override
    public EditorDto editorInfo(String id) throws Exception {
        return editorMapper.editorInfo(id);
    }

    @Override
    public EditorDto editorName(String id) throws SQLException {
        return editorMapper.editorName(id);
    }

    @Override
    public int regist(EditorDto editor) {
        editor.setPw(passwordService.encode(editor.getPw()));
        editor.setSalt(null);
        return editorMapper.regist(editor);
    }

    @Override
    public void saveRefreshToken(String id, String token) throws Exception {
        editorMapper.saveRefreshToken(Map.of("id", id, "token", token));
    }

    @Override
    public String getRefreshToken(String id) throws Exception {
        return (String) editorMapper.getRefreshToken(id);
    }

    @Override
    public void deleteRefreshToken(String id) throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("id", id);
        values.put("token", null);
        editorMapper.deleteRefreshToken(values);
    }

    @Override
    public int modify(EditorDto editor) {
        editor.setPw(passwordService.encode(editor.getPw()));
        editor.setSalt(null);
        return editorMapper.modify(editor);
    }

    @Override
    public int resign(String id) {
        return editorMapper.resign(id);
    }

    @Override
    public String getSalt(String id) {
        return editorMapper.getSalt(id);
    }

    @Override
    public List<EditorDto> power() throws Exception {
        return editorMapper.power();
    }
}
