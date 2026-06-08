package com.example.dailydairy.dairy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dailydairy.dairy.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);  // ✅ safer return type

    Optional<User> findByEmail(String email);        // ✅ already used in UserService
}
