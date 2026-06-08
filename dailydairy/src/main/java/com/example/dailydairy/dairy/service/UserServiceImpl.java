package com.example.dailydairy.dairy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dailydairy.dairy.model.User;
import com.example.dailydairy.dairy.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // ✅ New method to fetch user's display name (e.g., full name)
    @Override
    public String getDisplayNameByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getName) // or getUsername()
                .orElse("User");
    }
}
