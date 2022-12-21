package com.example.youtube_clone.service;


import com.example.youtube_clone.Exceptions.CustomErrorException;
import com.example.youtube_clone.model.Comment;
import com.example.youtube_clone.model.User;
import com.example.youtube_clone.model.Video;
import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.payload.request.VideoDto;
import com.example.youtube_clone.payload.response.ResponseHandler;
import com.example.youtube_clone.payload.response.UploadVideoResponse;
import com.example.youtube_clone.repository.UserRepository;
import com.example.youtube_clone.repository.VideoRepository;
import com.example.youtube_clone.security.jwt.AuthenticatedUser;
import com.example.youtube_clone.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;
    private final FilesStorageService filesStorageService;
    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    public ResponseEntity<Object> uploadVideo(HttpServletRequest request,
                                              MultipartFile video
    ) {
        System.out.println("videooooooo:"+video.getContentType());
        String video_url = "Uploads/";
        try {
                if(!video.getContentType().startsWith("video")){
                    throw new CustomErrorException("not valid video");
                }
                video_url +=video.getOriginalFilename();
                //upload video to server
                filesStorageService.save(video);
                //getUser
                User currUser = currentUser.getCurrentUser(request);

               //create new post
            Video newVideo = new Video();

            newVideo.setVideoUrl(video_url);
            newVideo.setAuthor(currUser);
            newVideo.setViewCount(0L);
            newVideo.setDisLikes(0L);
            newVideo.setLikes(0L);
            newVideo.setTitle(video.getOriginalFilename());

            videoRepository.save(newVideo);
         }catch (Exception exception){
              throw new CustomErrorException(HttpStatus.BAD_REQUEST,exception.getMessage());
         }
         return ResponseHandler.generateResponse("vide uploaded successfully ",
                HttpStatus.CREATED,
                null);

        //var video = new Video();
        //video.setVideoUrl(videoUrl);

  //      var savedVideo = videoRepository.save(video);
//        return new UploadVideoResponse(savedVideo.getId().toString(), savedVideo.getVideoUrl());

    }

    public VideoDto editVideo(VideoDto videoDto) {
        // Find the video by videoId
        var savedVideo = getVideoById(videoDto.getId());
        // Map the videoDto fields to video
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        // save the video  to the database
        videoRepository.save(savedVideo);
        return videoDto;
    }

//    public String uploadThumbnail(MultipartFile file, Long videoId) {
//        var savedVideo = getVideoById(videoId);
//
//        String thumbnailUrl = s3Service.uploadFile(file);
//
//        savedVideo.setThumbnailUrl(thumbnailUrl);
//
//        videoRepository.save(savedVideo);
//        return thumbnailUrl;
//    }

    Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by id - " + videoId));
    }

    public VideoDto getVideoDetails(Long videoId) {
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
        userService.addVideoToHistory(savedVideo);

        return mapToVideoDto(savedVideo);
    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    public VideoDto likeVideo(Long videoId) {
        Video videoById = getVideoById(videoId);

        if (userService.ifLikedVideo(videoById)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoById);
        } else if (userService.ifDisLikedVideo(videoById)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(videoById);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoById);
        } else {
            videoById.incrementLikes();
            userService.addToLikedVideos(videoById);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    public VideoDto disLikeVideo(Long videoId) {
        Video videoById = getVideoById(videoId);

        if (userService.ifDisLikedVideo(videoById)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(videoById);
        } else if (userService.ifLikedVideo(videoById)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoById);
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoById);
        } else {
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoById);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    private VideoDto mapToVideoDto(Video videoById) {
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getTitle());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setLikeCount(videoById.getLikes());
        videoDto.setDislikeCount(videoById.getDisLikes());
        videoDto.setViewCount(videoById.getViewCount());
        return videoDto;
    }

    public void addComment(HttpServletRequest request, @RequestBody CommentDto commentDto) {
        User currUser =currentUser.getCurrentUser(request);
        Video video = getVideoById(commentDto.getVideo_id());

        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(currUser);

        video.addComment(comment);

        videoRepository.save(video);
    }

    public List<Comment> getAllComments(Long videoId) {
        Video video = getVideoById(videoId);
        List<Comment> commentList = video.getCommentList();

        return commentList;
    }

//    private CommentDto mapToCommentDto(Comment comment) {
//        CommentDto commentDto = new CommentDto();
//        commentDto.setCommentText(comment.getText());
//        commentDto.setAuthorId(comment.getAuthorId());
//        return commentDto;
//    }

    public List<Video> getAllVideos() {

        return videoRepository.findAll();
    }
}