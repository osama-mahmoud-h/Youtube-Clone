package com.example.youtube_clone.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotBlank(message = "commnet cannot be empty")
    private String commentText;
    @NotBlank(message = "video cannot be empty")
    private String video_id;
}