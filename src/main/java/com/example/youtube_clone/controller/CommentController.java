package com.example.youtube_clone.controller;

import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/all/{video_id}")
    public ResponseEntity<?>getComments(@PathVariable("video_id") Long video_id) {
        return commentService.getComments(video_id);
    }
}
