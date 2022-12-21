package com.example.youtube_clone.repository;

import com.example.youtube_clone.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
