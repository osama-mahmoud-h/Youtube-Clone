package com.example.youtube_clone.payload.response;

import com.example.youtube_clone.model.Comment;
import com.example.youtube_clone.model.User;
import com.example.youtube_clone.model.VideoStatus;
import lombok.Data;
import lombok.ToString;

import javax.persistence.ElementCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@ToString
public class VideoDetailsDto {
    private Long id;
    private User author;
    private String title;
    private String description;
    private List<Comment> commentList = new ArrayList<>();
    private Long likes ;
    private Long disLikes ;
    private Set<String> tags;
    private String videoUrl;
    private VideoStatus videoStatus;
    private Long viewCount ;
    private String thumbnailUrl;

}
