package com.example.youtube_clone.service;



import com.example.youtube_clone.Exceptions.CustomErrorException;
import com.example.youtube_clone.model.Comment;
import com.example.youtube_clone.model.User;
import com.example.youtube_clone.model.Video;
import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.payload.request.VideoDto;
import com.example.youtube_clone.payload.response.ResponseHandler;
import com.example.youtube_clone.repository.UserRepository;
import com.example.youtube_clone.repository.VideoRepository;
import com.example.youtube_clone.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceImp implements VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;
    private final FilesStorageService filesStorageService;
    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    public ResponseEntity<Object> uploadVideo(HttpServletRequest request,
                                              MultipartFile video
    ) {
        String video_url = "Uploads/";
        try {
            if (!video.getContentType().startsWith("video")) {
                throw new CustomErrorException("not valid video");
            }
            video_url += video.getOriginalFilename();
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
        } catch (Exception exception) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        return ResponseHandler.generateResponse("vide uploaded successfully ",
                HttpStatus.CREATED,
                null);
    }

    public ResponseEntity<Object> editVideo(HttpServletRequest request, VideoDto videoDto) {
        // Find the video by videoId
        Video savedVideo = getVideoById(videoDto.getVideo_id());

        //get CurrentUser
        User currUser = currentUser.getCurrentUser(request);

        //check if  user.id == video.author.id
        if(!currUser.getId().equals(savedVideo.getId())){
            throw new CustomErrorException(HttpStatus.FORBIDDEN,"FORBIDDENENT!");
        }

        // Map the videoDto fields to video
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        //savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        // save the video  to the database
        videoRepository.save(savedVideo);
        return ResponseHandler.generateResponse("video get successfully",
                HttpStatus.ACCEPTED,
                savedVideo);
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

    private Video getVideoById(Long videoId) {
        Video video = videoRepository.findById(videoId).get();
       if(video==null){
           throw new CustomErrorException(HttpStatus.NOT_FOUND,
                   "Cannot find video by id "+video);
       }
       return video;
    }

    public ResponseEntity<Object> getVideoDetails(HttpServletRequest request, Long videoId) {
        Video savedVideo = getVideoById(videoId);
        //get currentUser
        User currUser = currentUser.getCurrentUser(request);

        increaseVideoCount(savedVideo);
        userService.addVideoToHistory(currUser,savedVideo);

        return ResponseHandler.generateResponse("video get successfully",
                HttpStatus.FOUND,
                savedVideo);
    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    public ResponseEntity<Object> likeVideo(HttpServletRequest request, Long videoId) {
        Video videoById = getVideoById(videoId);

        //get currentUser
        User currUser = currentUser.getCurrentUser(request);

        if (userService.ifLikedVideo(currUser,videoById)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(currUser,videoById);
        } else if (userService.ifDisLikedVideo(currUser,videoById)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(currUser,videoById);
            videoById.incrementLikes();
            userService.addToLikedVideos(currUser,videoById);
        } else {
            videoById.incrementLikes();
            userService.addToLikedVideos(currUser,videoById);
        }

        videoRepository.save(videoById);

        return ResponseHandler.generateResponse("video liked",
                HttpStatus.CREATED,
                null);
    }

    public ResponseEntity<Object> disLikeVideo(HttpServletRequest request, Long videoId) {
        Video videoById = getVideoById(videoId);
        //get currentUser
        User currUser = currentUser.getCurrentUser(request);

        if (userService.ifDisLikedVideo(currUser,videoById)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(currUser,videoById);
        } else if (userService.ifLikedVideo(currUser,videoById)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(currUser,videoById);
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(currUser,videoById);
        } else {
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(currUser,videoById);
        }

        videoRepository.save(videoById);

        return ResponseHandler.generateResponse("video disliked",
                HttpStatus.CREATED,
                null);
    }

//    private VideoDto mapToVideoDto(Video videoById) {
//        VideoDto videoDto = new VideoDto();
//        videoDto.setVideoUrl(videoById.getVideoUrl());
//        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
//        videoDto.setId(videoById.getId());
//        videoDto.setTitle(videoById.getTitle());
//        videoDto.setDescription(videoById.getDescription());
//        videoDto.setTags(videoById.getTags());
//        videoDto.setVideoStatus(videoById.getVideoStatus());
//        videoDto.setLikeCount(videoById.getLikes());
//        videoDto.setDislikeCount(videoById.getDisLikes());
//        videoDto.setViewCount(videoById.getViewCount());
//        return videoDto;
//    }

    public void addComment(HttpServletRequest request, @RequestBody CommentDto commentDto) {
        User currUser = currentUser.getCurrentUser(request);
        Video video = getVideoById(commentDto.getVideo_id());

        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(currUser);

        video.addComment(comment);

        videoRepository.save(video);
    }

    public ResponseEntity<Object> getAllComments(Long videoId) {
        Video video = getVideoById(videoId);
        List<Comment> commentList = video.getCommentList();

        return ResponseHandler.generateResponse("all Comments get",
                HttpStatus.FOUND,
                commentList);
    }

//    private CommentDto mapToCommentDto(Comment comment) {
//        CommentDto commentDto = new CommentDto();
//        commentDto.setCommentText(comment.getText());
//        commentDto.setAuthorId(comment.getAuthorId());
//        return commentDto;
//    }

    public ResponseEntity<Object> getAllVideos() {
        return ResponseHandler.generateResponse("video liked",
                HttpStatus.CREATED,
                videoRepository.findAll());
    }
}