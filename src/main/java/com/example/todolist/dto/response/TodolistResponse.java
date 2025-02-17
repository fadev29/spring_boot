package com.example.todolist.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TodolistResponse {
    private Long id;
    private String title;
    private String description;
    private String username;
    private CategoryData categoryId;
    private Boolean isCompleted;
    private LocalDateTime deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imagePath;


    public TodolistResponse(){
        this.categoryId = new CategoryData();
    }

    public  void  setCategoryId(Long id){
        this.categoryId.setId(id);
    }

    public  void  setCategoryName(String name){
        this.categoryId.setName(name);
    }
    // object category
    @Data
    class CategoryData{
        private Long id;
        private  String name;
    }
}
