package com.example.todolist.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TodolistRequest {
   private String title;
   private String description;
   private String username;
   private Long categoryId;
   private Boolean isClompleted;
   private LocalDateTime deleteAt;
   private MultipartFile imagePath;
}
