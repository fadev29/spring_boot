package com.example.todolist.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class CategoryRequest {
    @NotBlank
    @Size(max = 50)
    private String name;
}
