package com.example.todolist.repository;

import com.example.todolist.model.Todolist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodolistRepository extends JpaRepository<Todolist, Long> {
    Optional<Todolist> findById(Long id);
    // select * from todolist where lower(title)
    List<Todolist> findByTitleContainingIgnoreCase(String title);
    List<Todolist> findByCategoryId(Long categoryId);
    List<Todolist> findByUserId(UUID userId);
    Page<Todolist> findAllByDeletedIsNull(Pageable pageable);
}
