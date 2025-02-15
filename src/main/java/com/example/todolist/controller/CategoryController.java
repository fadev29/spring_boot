package com.example.todolist.controller;

import com.example.todolist.dto.request.CategoryRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.CategoryResponse;
import com.example.todolist.exception.DataNotFoundException;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todolist/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?>getAllCategory(){ // ? : buat ngembaliin response bebas
        try {
            List<CategoryResponse> responses = categoryService.findAll();
            return ResponseEntity.status(HttpStatus.OK.value()).body(new ApiResponse<>(HttpStatus.OK.value(), responses));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));

        }
    }
    @PostMapping
    public ResponseEntity<?>crateCategory(@RequestBody CategoryRequest categoryRequest) { // ? : buat ngembaliin response bebas
        try {
            CategoryResponse responses = categoryService.create(categoryRequest);
            return ResponseEntity.status(HttpStatus.OK.value()).body(new ApiResponse<>(HttpStatus.OK.value(), responses));
        }catch (DuplicateDataException e){
            return ResponseEntity.status(HttpStatus.CONFLICT.value())
                    .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?>updateCategory(@PathVariable("id") Long id, @RequestBody CategoryRequest categoryRequest) { // ? : buat ngembaliin response bebas
       try{
           CategoryResponse categoryResponse = categoryService.updateCategory(id, categoryRequest);
           return  ResponseEntity.status(HttpStatus.OK.value()).body(new ApiResponse<>(HttpStatus.OK.value(), categoryResponse));
       }catch (DataNotFoundException e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                   .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
       }catch (DuplicateDataException e){
           return ResponseEntity.status(HttpStatus.CONFLICT.value())
                   .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
       }
       catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                   .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
       }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteCategory(@PathVariable Long id) {
        try{
            categoryService.deleteCategory(id);
            return  ResponseEntity.status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), "Category deleted successfully"));
        }catch (DataNotFoundException  e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                   .body(new ApiResponse<>(HttpStatus.OK.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Delete failed"));
        }
    }

    @GetMapping("/name")
    public ResponseEntity<?>getCategoryByName(@RequestParam String categoryname) {
            Optional<CategoryResponse> categoryResponse = categoryService.findByName(categoryname);
            return ResponseEntity.status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), categoryResponse));
    }
}
