package com.example.todolist.dto.response;

import lombok.*;
import java.time.LocalDateTime;
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
