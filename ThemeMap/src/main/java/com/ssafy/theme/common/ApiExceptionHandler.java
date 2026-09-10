package com.ssafy.theme.common;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("요청 값을 확인해 주세요.");
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<?> duplicate(DuplicateKeyException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 등록된 값입니다."));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> unexpected(Exception exception) {
        log.error("Unhandled request failure", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "요청을 처리하지 못했습니다."));
    }
}
