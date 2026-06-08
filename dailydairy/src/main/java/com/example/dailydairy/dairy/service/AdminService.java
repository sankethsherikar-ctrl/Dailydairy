package com.example.dailydairy.dairy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dailydairy.dairy.model.Admin;
import com.example.dailydairy.dairy.repositories.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private PasswordEncoder encoder;

    // ✅ Safe find method – pick first if multiple exist
    public Optional<Admin> findByEmailOrName(String input) {
        List<Admin> byEmail = adminRepo.findByEmail(input);
        if (!byEmail.isEmpty()) {
            return Optional.of(byEmail.get(0));  // Use the first match
        }

        List<Admin> byName = adminRepo.findByName(input);
        if (!byName.isEmpty()) {
            return Optional.of(byName.get(0));
        }

        return Optional.empty(); // No match
    }

    // ✅ Save new admin with encoded password
    public void save(Admin admin) {
        if (admin.getPassword() != null && !admin.getPassword().startsWith("$2a$")) { // bcrypt check
            admin.setPassword(encoder.encode(admin.getPassword()));
        }
        adminRepo.save(admin);
    }
}
