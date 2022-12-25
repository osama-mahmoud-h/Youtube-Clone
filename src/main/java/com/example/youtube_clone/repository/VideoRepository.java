package com.example.youtube_clone.repository;

import com.example.youtube_clone.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findById(Long videoId);
}
