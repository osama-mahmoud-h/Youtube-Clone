package com.example.youtube_clone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String username;
    private String email;
    private String password;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<User> subscribedToUsers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Set<User> subscribers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Video>createdVideos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    @JsonIgnore
   // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Video> videoHistory = ConcurrentHashMap.newKeySet();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "user_likes",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "video_id") })
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Set<Video> likedVideos = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "user_dislikes",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "video_id") })
    @JsonIgnore
  //  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Video> disLikedVideos = new HashSet();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL}
    )
    @JsonIgnore
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Role> roles = new HashSet<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void addToLikeVideos(Video video) {
         likedVideos.add(video);
    }

    public boolean removeFromLikedVideos(Long videoId) {
        boolean removed =  likedVideos.removeIf(video->video.getId().equals(videoId));
        return removed;
    }

    public void removeFromDislikedVideos(Long videoId) {
        disLikedVideos.removeIf(video->video.getId().equals(videoId));
    }

    public void addToDislikedVideos(Video videoId) {
        disLikedVideos.add(videoId);
    }

    public void addToVideoHistory(Video videoId) {
        videoHistory.add(videoId);
    }

    public void addToSubscribedToUsers(User userId) {
        subscribedToUsers.add(userId);
    }

    public void addToSubscribers(User userId) {
        subscribers.add(userId);
    }

    public void removeFromSubscribedToUsers(Long userId) {
        subscribedToUsers.removeIf(usr->usr.getId().equals(userId));
    }

    public void removeFromSubscribers(Long userId) {
        subscribers.remove(userId);
    }
}
