package com.example.todolist.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class UserRequest {
    @NotBlank
    @Size(max = 50)
    private String username;
    @NotBlank
    @Size(max = 255)
    private String password;
    @NotBlank
    @Size(max = 100)
    @Email
    private String email;
    @NotBlank
    @Size(max = 10)
    private String role;
}
