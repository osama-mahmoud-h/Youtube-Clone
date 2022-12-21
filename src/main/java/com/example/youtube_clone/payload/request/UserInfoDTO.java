package com.example.youtube_clone.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private String id;
    private String sub;
    private String givenName;
    private String familyName;
    private String name;
    private String picture;
    private String email;
}