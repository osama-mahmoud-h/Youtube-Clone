package com.example.youtube_clone.service;

import com.example.youtube_clone.payload.request.LoginRequest;
import com.example.youtube_clone.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    ResponseEntity<?> getSubscribersCount(Long user_id);

    ResponseEntity<?> chanelsUserSubscribed(HttpServletRequest request);

    ResponseEntity<?> login(LoginRequest loginRequest);

    ResponseEntity<?> register(SignupRequest signUpRequest);
}
