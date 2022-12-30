package com.example.youtube_clone.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.example.youtube_clone.Exceptions.CustomErrorException;
import com.example.youtube_clone.model.User;
import com.example.youtube_clone.payload.response.ResponseHandler;
import com.example.youtube_clone.security.jwt.CurrentUser;
import com.example.youtube_clone.service.UserService;
import com.example.youtube_clone.service.imp.UserServiceImp;
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
  private final CurrentUser currentUser;

  @GetMapping("/test")
  public ResponseEntity<?> test(HttpServletRequest request) {
    System.out.println("request: "+currentUser.getCurrentUser(request));
    User currUser = currentUser.getCurrentUser(request);
    if(currUser==null){
      throw new CustomErrorException(HttpStatus.NOT_FOUND,"user not found");
    }
    return ResponseHandler.generateResponse("curr user",HttpStatus.FOUND,currUser);
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
