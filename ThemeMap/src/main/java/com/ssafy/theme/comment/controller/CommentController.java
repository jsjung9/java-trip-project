package com.ssafy.theme.comment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.theme.comment.dto.CommentDto;
import com.ssafy.theme.comment.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/regist")
    public ResponseEntity<Void> registComment(@Valid @RequestBody CommentDto comment) throws Exception {
        commentService.registComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/comments/{placeId}")
    public List<CommentDto> commentsOfPlace(@PathVariable String placeId) throws Exception {
        return commentService.commentsOfPlace(placeId);
    }
}
