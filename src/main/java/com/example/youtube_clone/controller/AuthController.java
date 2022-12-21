package com.example.youtube_clone.controller;

import javax.validation.Valid;

import com.example.youtube_clone.Exceptions.CustomErrorException;
import com.example.youtube_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.youtube_clone.payload.request.LoginRequest;
import com.example.youtube_clone.payload.request.SignupRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  @PostMapping("/test")
  public ResponseEntity<?> test(@Valid @RequestBody LoginRequest loginRequest) {
    throw new CustomErrorException( HttpStatus.BAD_REQUEST,"Email is already in use!");
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return userService.login(loginRequest);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    return userService.register(signUpRequest);
  }
}
