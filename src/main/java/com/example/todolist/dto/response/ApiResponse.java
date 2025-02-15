package com.example.todolist.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private   int status;
    private  T data; // T generic type(tipe data bebas)
}
