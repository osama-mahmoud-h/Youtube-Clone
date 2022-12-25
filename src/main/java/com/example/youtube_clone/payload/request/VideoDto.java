package com.example.youtube_clone.payload.request;


import com.example.youtube_clone.model.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDto {
    @NotBlank(message = "you should provide all fileds")
    private Long video_id;
    private String title;
    private String description;
    private VideoStatus videoStatus;
    private String thumbnailUrl;
}