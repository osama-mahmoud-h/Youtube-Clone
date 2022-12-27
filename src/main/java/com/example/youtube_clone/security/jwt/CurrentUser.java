package com.example.youtube_clone.security.jwt;

import com.example.youtube_clone.model.User;
import com.example.youtube_clone.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final AuthenticatedUser authenticatedUser;
    private final UserRepository userRepository;

    public User getCurrentUser(HttpServletRequest request){
        Long user_id = Long.parseLong(authenticatedUser.userData(request).get("user_id"));
        User currentUser = userRepository.findUserById(user_id).get();
        return currentUser;
    }
}
