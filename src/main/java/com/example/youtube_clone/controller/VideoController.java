package com.example.youtube_clone.controller;

import com.example.youtube_clone.model.Comment;
import com.example.youtube_clone.model.Video;
import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.payload.request.VideoDto;
import com.example.youtube_clone.payload.response.UploadVideoResponse;
import com.example.youtube_clone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
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
    public VideoDto editVideoMetadata(@RequestBody VideoDto videoDto) {
        return videoService.editVideo(videoDto);
    }

    @GetMapping("/{videoId}")
    public VideoDto getVideoDetails(@PathVariable Long videoId) {
        return videoService.getVideoDetails(videoId);
    }

    @PostMapping("/{videoId}/like")
    public VideoDto likeVideo(@PathVariable Long videoId) {
        return videoService.likeVideo(videoId);
    }

    @PostMapping("/{videoId}/disLike")
    public VideoDto disLikeVideo(@PathVariable Long videoId) {
        return videoService.disLikeVideo(videoId);
    }

    @PostMapping("/{videoId}/comment")
    public void addComment(HttpServletRequest request, @RequestBody CommentDto commentDto) {
        videoService.addComment(request, commentDto);
    }

    @GetMapping("/{videoId}/comment")
    public List<Comment> getAllComments(@PathVariable Long videoId) {
        return videoService.getAllComments(videoId);
    }

    @GetMapping
    public List<Video> getAllVideos() {
        return videoService.getAllVideos();
    }

}