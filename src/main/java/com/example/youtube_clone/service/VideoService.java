package com.example.youtube_clone.service;

import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.payload.request.VideoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface VideoService {
    public ResponseEntity<Object> uploadVideo(HttpServletRequest request,
                                              MultipartFile video,
                                               String title,
                                               String description
    );
    public ResponseEntity<Object> editVideo(HttpServletRequest request, VideoDto videoDto);
    public ResponseEntity<Object> getVideoDetails(HttpServletRequest request, Long videoId);
    public ResponseEntity<Object> likeVideo(HttpServletRequest request, Long videoId);
    public ResponseEntity<Object> disLikeVideo(HttpServletRequest request, Long videoId);
    public ResponseEntity<Object> getAllComments(Long videoId);
    public ResponseEntity<Object> getAllVideos();
}
