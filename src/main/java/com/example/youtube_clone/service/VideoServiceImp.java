package com.example.youtube_clone.service;



import com.example.youtube_clone.Exceptions.CustomErrorException;
import com.example.youtube_clone.model.Comment;
import com.example.youtube_clone.model.User;
import com.example.youtube_clone.model.Video;
import com.example.youtube_clone.payload.request.VideoDto;
import com.example.youtube_clone.payload.response.ResponseHandler;
import com.example.youtube_clone.repository.UserRepository;
import com.example.youtube_clone.repository.VideoRepository;
import com.example.youtube_clone.security.jwt.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VideoServiceImp implements VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;
    private final FilesStorageService filesStorageService;
    private final AuthenticatedUser authenticatedUser;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<Object> uploadVideo(HttpServletRequest request,
                                              MultipartFile video
//                                              String title,
//                                              String description
    ) {
        String video_url = "/public/videos/";
        try {
            if (!video.getContentType().startsWith("video")) {
                throw new CustomErrorException("not valid video");
            }
            String random = String.valueOf(new Random().nextLong());
            video_url += random+"."+video.getOriginalFilename();
            //upload video to server
            filesStorageService.save(video,random);
            //getUser
            User currUser = authenticatedUser.getCurrentUser(request).get();

            //create new post
            Video newVideo = new Video();

            newVideo.setVideoUrl(video_url);
            newVideo.setViewCount(0L);
            newVideo.setDisLikes(0L);
            newVideo.setLikes(0L);
            newVideo.setTitle(video.getName());
          //  newVideo.setDescription(description);

            currUser.getCreatedVideos().add(newVideo);

            userRepository.save(currUser);
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
        User currUser = authenticatedUser.getCurrentUser(request).get();

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
        Optional<Video> video = videoRepository.findById(videoId);
       if(video.isEmpty()){
           throw new CustomErrorException(HttpStatus.NOT_FOUND,
                   "Cannot find video by id "+video);
       }
       return video.get();
    }

    public ResponseEntity<Object> getVideoDetails(HttpServletRequest request, Long videoId) {
        Video savedVideo = getVideoById(videoId);
        //get currentUser
        Optional<User> currUser = authenticatedUser.getCurrentUser(request);

        increaseVideoCount(savedVideo);

      userService.addVideoToHistory(currUser.get(),savedVideo);

        return ResponseHandler.generateResponse("video get successfully",
                HttpStatus.OK,
                savedVideo);
    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    public ResponseEntity<Object> likeVideo(HttpServletRequest request, Long videoId) {
        Video videoById = getVideoById(videoId);
        User currUser = authenticatedUser.getCurrentUser(request).get();
      //  userService.removeFromLikedVideos(currUser,videoId);

        if (userService.ifDisLikedVideo(currUser,videoById)) {
            System.out.println("from like function: , disliked before");
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(currUser,videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(currUser,videoById);
        } else if (userService.ifLikedVideo(currUser,videoById)) {
            System.out.println("liked bfore======");
            videoById.decrementLikes();
            userService.removeFromLikedVideos(currUser,videoId);
        } else {
            System.out.println("like case3: ");
            videoById.incrementLikes();
            userService.addToLikedVideos(currUser,videoById);
        }

      //  videoRepository.save(videoById);

        return ResponseHandler.generateResponse("video liked",
                HttpStatus.CREATED,
                videoById);
    }

    public ResponseEntity<Object> disLikeVideo(HttpServletRequest request, Long videoId) {
        Video videoById = getVideoById(videoId);
        //get currentUser
        User currUser = authenticatedUser.getCurrentUser(request).get();

        System.out.println("user like count"+currUser.getLikedVideos().size()+" , "+
                userService.ifLikedVideo(currUser,videoById));
        System.out.println("user dislike count"+currUser.getDisLikedVideos().size());
      //  userService.removeFromLikedVideos(currUser,videoId);

        if (userService.ifLikedVideo(currUser,videoById)) {
            System.out.println("dilike -> case1:");
            videoById.decrementLikes();
            userService.removeFromLikedVideos(currUser,videoId);
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(currUser,videoById);

        } else if (userService.ifDisLikedVideo(currUser,videoById)) {
            System.out.println("dislike -> case2 : from dislike: , liked before");
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(currUser,videoId);
        } else {
            System.out.println("case:3");
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(currUser,videoById);
        }

       // videoRepository.save(videoById);

        return ResponseHandler.generateResponse("video disliked",
                HttpStatus.CREATED,
                videoById);
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
        List<Video> allVideos = videoRepository.findAll();
        System.out.println("videos: "+allVideos.size());
        return ResponseHandler.generateResponse("all videos get ",
                HttpStatus.CREATED,
                allVideos);
    }
}