package com.example.dailydairy.dairy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dailydairy.dairy.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    List<Admin> findByEmail(String email);
    List<Admin> findByName(String name);
}
