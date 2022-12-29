package com.example.youtube_clone.service;

import com.example.youtube_clone.Exceptions.CustomErrorException;
import com.example.youtube_clone.model.*;
import com.example.youtube_clone.payload.request.LoginRequest;
import com.example.youtube_clone.payload.request.SignupRequest;
import com.example.youtube_clone.payload.response.JwtResponse;
import com.example.youtube_clone.payload.response.ResponseHandler;
import com.example.youtube_clone.repository.RoleRepository;
import com.example.youtube_clone.repository.UserRepository;
import com.example.youtube_clone.security.jwt.AuthenticatedUser;
import com.example.youtube_clone.security.jwt.JwtUtils;
import com.example.youtube_clone.security.securityServices.UserDetailsImpl;
import com.example.youtube_clone.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;

    private final AuthenticatedUser authenticatedUser;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public ResponseEntity<Object>login(@Valid @RequestBody LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<Object> register(@Valid @RequestBody SignupRequest signUpRequest){

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST,"Email is already in use!");
        }
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();

        roles.add(new Role(ERole.ROLE_USER));
        user.setRoles(roles);
        userRepository.save(user);
        user.setPassword("");
        return ResponseHandler
                .generateResponse("User registered successfully!", HttpStatus.CREATED,user);
    }

    public User getCurrentUser() {
        return new User();
    }

    public void addToLikedVideos(User currUser,Video video) {
        currUser.addToLikeVideos(video);
        userRepository.save(currUser);
    }

    public boolean ifLikedVideo(User currUser, Video videoId) {
        Video video = currUser.getLikedVideos().stream().
                filter(likedVideo -> likedVideo.getId().equals(videoId.getId()))
                .findAny()
                .orElse(null);

        System.out.println("liked before: "+video);
        return video!=null?true:false;//currUser.getLikedVideos().stream().anyMatch(likedVideo -> likedVideo.getId().equals(videoId.getId()));
    }

    public boolean ifDisLikedVideo(User currUser,Video videoId) {
        Video video = currUser.getDisLikedVideos().stream().
                filter(likedVideo -> likedVideo.getId().equals(videoId.getId()))
                .findAny()
                .orElse(null);
        System.out.println("disLiked before?: "+video);

        return video != null ? true : false;
    }

    public void removeFromLikedVideos(User currUser,Long videoId) {

        currUser.removeFromLikedVideos(videoId);
        userRepository.save(currUser);
    }

    public void removeFromDislikedVideos(User currUser,Long videoId) {
        currUser.removeFromDislikedVideos(videoId);
        userRepository.save(currUser);
    }

    public void addToDisLikedVideos(User currUser,Video videoId) {
        currUser.addToDislikedVideos(videoId);
        userRepository.save(currUser);
    }

    public void addVideoToHistory(User currUser,Video video) {
        currUser.addToVideoHistory(video);
        userRepository.save(currUser);
    }

    public boolean isVideoAuthor(User user, Long video_d){
        Video video = user.getCreatedVideos().
                stream()
                .filter(vd ->vd.getId().equals(video_d))
                .findFirst()
                .orElse(null);

        return video==null ? false : true;
    }

    public void subscribeUser(User user) {
        User currentUser = getCurrentUser();
        currentUser.addToSubscribedToUsers(user);
        user.addToSubscribers(currentUser);

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public void unSubscribeUser(User user) {
        User currentUser = getCurrentUser();
        currentUser.removeFromSubscribedToUsers(user.getId());

        user.removeFromSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public ResponseEntity<?> getSubscribersCount(Long user_id){
        Optional<User> channel  = userRepository.findUserById(user_id);
        if(channel.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"channel not found");
        }
        return ResponseHandler.generateResponse("count of subscribers",
                HttpStatus.OK,
                channel.get().getSubscribers().size()
                );
    }

    public ResponseEntity<?> chanelsUserSubscribed(HttpServletRequest request){
        Optional<User> currUser =  authenticatedUser.getCurrentUser(request);

        Set<User> channels=  currUser.get().getSubscribedToUsers();

        return ResponseHandler.generateResponse("channles user subscribed",
                HttpStatus.OK,
                channels
        );
    }

    public Set<Video> userHistory(User user) {
        return user.getVideoHistory();
    }


}