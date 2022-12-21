package com.example.youtube_clone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String username;
    private String email;
    private String password;

    @Transient
    private Set<User> subscribedToUsers = ConcurrentHashMap.newKeySet();

    @Transient
    private Set<User> subscribers = ConcurrentHashMap.newKeySet();
    @Transient
    private Set<Video> videoHistory = ConcurrentHashMap.newKeySet();
    @Transient
    private Set<Video> likedVideos = ConcurrentHashMap.newKeySet();
    @Transient
    private Set<Video> disLikedVideos = ConcurrentHashMap.newKeySet();

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL}
    )
    private Set<Role> roles = new HashSet<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void addToLikeVideos(Video videoId) {
        likedVideos.add(videoId);
    }

    public void removeFromLikedVideos(Video videoId) {
        likedVideos.remove(videoId);
    }

    public void removeFromDislikedVideos(Long videoId) {
        disLikedVideos.remove(videoId);
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
        subscribedToUsers.remove(userId);
    }

    public void removeFromSubscribers(Long userId) {
        subscribers.remove(userId);
    }
}
