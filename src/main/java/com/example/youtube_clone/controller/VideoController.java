package com.example.youtube_clone.controller;

import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.payload.request.VideoDto;
import com.example.youtube_clone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class VideoController {
    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadVideo(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return videoService.uploadVideo(request,file);
    }

    @PostMapping("/thumbnail")
    public String uploadThumbnail(@RequestParam("image") MultipartFile image, @RequestParam("videoId") Long videoId) {
        return null; //videoService.uploadThumbnail(image, videoId);
    }

    @PutMapping
    public ResponseEntity<Object> editVideoMetadata(HttpServletRequest request, @RequestBody VideoDto videoDto) {
        return videoService.editVideo(request,videoDto);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<Object> getVideoDetails(HttpServletRequest request, @PathVariable("videoId") Long videoId) {
        return videoService.getVideoDetails(request,videoId);
    }

    @PostMapping("/{videoId}/like")
    public ResponseEntity<Object> likeVideo(HttpServletRequest request, @PathVariable Long videoId) {
        return videoService.likeVideo(request,videoId);
    }

    @PostMapping("/{videoId}/disLike")
    public ResponseEntity<Object> disLikeVideo(HttpServletRequest request, @PathVariable Long videoId) {
        return videoService.disLikeVideo(request,videoId);
    }

//    @GetMapping("/{videoId}/comment")
//    public ResponseEntity<Object> getAllComments(@PathVariable Long videoId) {
//        return videoService.getAllComments(videoId);
//    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllVideos() {
        return videoService.getAllVideos();
    }

}