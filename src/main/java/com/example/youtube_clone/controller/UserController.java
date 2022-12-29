package com.example.youtube_clone.controller;

import com.example.youtube_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/channel-subscriber/{user_id}")
    public ResponseEntity<?> getSubscribersCount(@PathVariable("user_id") Long user_id){
        return userService.getSubscribersCount(user_id);
    }

    @GetMapping("/channels-user-subscribed")
    public ResponseEntity<?> chanelsUserSubscribed(HttpServletRequest request){
        return userService.chanelsUserSubscribed(request);
    }

}
