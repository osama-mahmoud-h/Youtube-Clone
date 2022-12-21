package com.example.youtube_clone.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
	@NotBlank(message = "please provide username")
  private String username;

	@NotBlank(message = "please provide password")
	private String password;

}
