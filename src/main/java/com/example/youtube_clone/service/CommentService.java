package com.example.youtube_clone.service;

import com.example.youtube_clone.payload.request.CommentDto;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    ResponseEntity<?> addComment(HttpServletRequest request, CommentDto commentDto);

    ResponseEntity<?> getComments(Long video_id);
}
