package com.example.youtube_clone.service.imp;

import com.example.youtube_clone.Exceptions.CustomErrorException;
import com.example.youtube_clone.model.Comment;
import com.example.youtube_clone.model.User;
import com.example.youtube_clone.model.Video;
import com.example.youtube_clone.payload.request.CommentDto;
import com.example.youtube_clone.payload.response.ResponseHandler;
import com.example.youtube_clone.repository.CommentRepository;
import com.example.youtube_clone.repository.VideoRepository;
import com.example.youtube_clone.security.jwt.AuthenticatedUser;
import com.example.youtube_clone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final AuthenticatedUser authenticatedUser;
    private final VideoRepository videoRepository;

    public ResponseEntity<?> addComment(HttpServletRequest request, @RequestBody CommentDto commentDto) {
        Optional<User> currUser = authenticatedUser.getCurrentUser(request);

        Optional<Video> video = videoRepository.findById(Long.parseLong(commentDto.getVideo_id()));
        if(video.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"video not found");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        //comment.setAuthorId(currUser.get());
        video.get().getCommentList().add(comment);

   //     commentRepository.save(comment);
        videoRepository.save(video.get());
        return ResponseHandler.generateResponse("comment added success",
                HttpStatus.CREATED,
                comment);
    }

    public ResponseEntity<?>getComments(@PathVariable("video_id") Long video_id){
        Optional<Video> video = videoRepository.findById(video_id);
        if(video.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"video not found");
        }

        List<Comment> allComments = video.get().getCommentList();
        return ResponseHandler.generateResponse("comment added success",
                HttpStatus.CREATED,
                allComments);
    }

}
