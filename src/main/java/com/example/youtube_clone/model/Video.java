package com.example.youtube_clone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.util.Lazy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Data
@ToString
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    private String description;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> commentList = new ArrayList<>();

    private Long likes ;

    private Long disLikes ;

    @ElementCollection
    private Set<String> tags;

    private String videoUrl;

    private VideoStatus videoStatus;

    private Long viewCount ;

    private String thumbnailUrl;

    public void incrementLikes() {
        likes++;
    }

    public void decrementLikes() {
        likes--;
    }

    public void incrementDisLikes() {
        disLikes++;
    }

    public void decrementDisLikes() {
        disLikes--;
    }

    public void incrementViewCount() {
        viewCount++;
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }
}
