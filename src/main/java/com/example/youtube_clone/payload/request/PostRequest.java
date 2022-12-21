package com.example.youtube_clone.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String text;
    private MultipartFile []files;
}
