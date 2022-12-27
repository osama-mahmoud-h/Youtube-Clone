package com.example.youtube_clone.controller;

import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(HttpServletRequest request,
                                        @RequestBody CommentDto commentDto
    ) {
        return commentService.addComment(request,commentDto);
    }
}
