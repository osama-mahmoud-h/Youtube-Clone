package com.example.youtube_clone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.youtube_clone.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserByEmail(String email);

     User findUserById(Long id);

  //Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  //  Optional<User> findBySub(String sub);
}
