package com.ssafy.theme.editor.controller;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.theme.editor.dto.EditorDto;
import com.ssafy.theme.editor.service.EditorService;
import com.ssafy.theme.util.JWTUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/editor")
@Validated
public class EditorController {
    private static final String REFRESH_COOKIE = "refresh_token";

    private final EditorService service;
    private final JWTUtil jwtUtil;
    private final boolean secureCookie;

    public EditorController(EditorService service, JWTUtil jwtUtil,
            @Value("${security.cookie.secure:false}") boolean secureCookie) {
        this.service = service;
        this.jwtUtil = jwtUtil;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody EditorDto credentials) throws Exception {
        EditorDto editor = service.login(credentials);
        if (editor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디 또는 비밀번호를 확인해 주세요."));
        }
        String accessToken = jwtUtil.createAccessToken(editor.getId());
        String refreshToken = jwtUtil.createRefreshToken(editor.getId());
        service.saveRefreshToken(editor.getId(), refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(refreshToken).toString())
                .body(Map.of("access-token", accessToken, "editor", editor));
    }

    @GetMapping({"/info", "/info/{ignoredId}"})
    public Map<String, Object> getEditorInfo(Authentication authentication,
            @PathVariable(required = false) String ignoredId) throws Exception {
        return Map.of("editorInfo", service.editorInfo(authentication.getName()));
    }

    @GetMapping("/name/{editorId}")
    public Map<String, Object> getEditorName(@PathVariable String editorId) throws Exception {
        return Map.of("name", service.editorName(editorId));
    }

    @PostMapping("/regist")
    public ResponseEntity<?> regist(@Valid @RequestBody EditorDto editor) {
        service.regist(editor);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws Exception {
        String refreshToken = readCookie(request, REFRESH_COOKIE);
        try {
            String id = jwtUtil.getRefreshTokenSubject(refreshToken);
            if (!refreshToken.equals(service.getRefreshToken(id))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String rotatedRefreshToken = jwtUtil.createRefreshToken(id);
            service.saveRefreshToken(id, rotatedRefreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie(rotatedRefreshToken).toString())
                    .body(Map.of("access-token", jwtUtil.createAccessToken(id)));
        } catch (JwtException | IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) throws Exception {
        service.deleteRefreshToken(authentication.getName());
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
                .build();
    }

    @PatchMapping("/modify")
    public ResponseEntity<?> modify(Authentication authentication, @Valid @RequestBody EditorDto editor) {
        editor.setId(authentication.getName());
        return service.modify(editor) > 0
                ? ResponseEntity.ok(Map.of("message", "회원정보가 수정되었습니다."))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/resign")
    public ResponseEntity<Void> resign(Authentication authentication) {
        return service.resign(authentication.getName()) > 0
                ? ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString()).build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/power")
    public List<EditorDto> power() throws Exception {
        return service.power();
    }

    private ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true).secure(secureCookie).sameSite("Strict").path("/editor")
                .maxAge(Duration.ofSeconds(jwtUtil.getRefreshTokenMaxAgeSeconds())).build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true).secure(secureCookie).sameSite("Strict").path("/editor").maxAge(0).build();
    }

    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
