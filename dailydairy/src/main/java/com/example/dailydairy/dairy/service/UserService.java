package com.example.dailydairy.dairy.service;

import com.example.dailydairy.dairy.model.User;

public interface UserService {
    void saveUser(User user);
    User findByEmail(String email);

    // ✅ Add this
    String getDisplayNameByEmail(String email);
}
