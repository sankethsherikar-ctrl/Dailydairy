package com.example.dailydairy.dairy.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dailydairy.dairy.model.DiaryNote;
import com.example.dailydairy.dairy.model.User;

@Repository
public interface DiaryNoteRepository extends JpaRepository<DiaryNote, Long> {
    Optional<DiaryNote> findByUserAndDate(User user, LocalDate date);
    List<DiaryNote> findByUserOrderByDateDesc(User user);
    long countByUser(User user);
}
