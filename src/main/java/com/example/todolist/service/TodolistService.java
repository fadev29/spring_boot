package com.example.todolist.service;

import com.example.todolist.dto.request.TodolistRequest;
import com.example.todolist.dto.response.TodolistResponse;
import com.example.todolist.exception.DataNotFoundException;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.model.Category;
import com.example.todolist.model.Todolist;
import com.example.todolist.model.User;
import com.example.todolist.repository.CategoryRepository;
import com.example.todolist.repository.TodolistRepository;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TodolistService {
    @Autowired
    private TodolistRepository todolistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private static String imageDirectory = "src/main/resources/static/images/";

    private static long maxFileSize = 5 * 1024 * 1024; // 5mb
    private static String[] allowedFileTypes = {"image/jpeg", "image/png", "image/jpg"}; //format yang diizinkan

    @jakarta.transaction.Transactional
    public TodolistResponse create(TodolistRequest todolistRequest) {
        try {
            Todolist todolist = new Todolist();
            todolist.setTitle(todolistRequest.getTitle());
            todolist.setDescription(todolistRequest.getDescription());
            User user = userRepository.findByUsername(todolistRequest.getUsername())
                            .orElseThrow(() -> new RuntimeException("user not found"));
            todolist.setUser(user);
            Category category = categoryRepository.findById(todolistRequest.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));
            todolist.setCategory(category);
            todolist.setIsCompleted(todolistRequest.getIsClompleted());
            if(todolistRequest.getImagePath() != null && !todolistRequest.getImagePath().isEmpty()) {
                MultipartFile file = todolistRequest.getImagePath();

                if(file.getSize() > maxFileSize) {
                    throw new RuntimeException("File size exceeds the maximum limit of " + maxFileSize / (1024 * 1024) + "MB");
                }

                String fileType = file.getContentType();
                boolean isValidType = false;
                for (String allowedType : allowedFileTypes) {
                    if (allowedType.equals(fileType)) {
                        isValidType = true;
                        break;
                    }
                }

                if(!isValidType) {
                    throw new RuntimeException("Invalid file type. Only JPEG, PNG, and JPG files are allowed.");
                }

                String originalFilename = file.getOriginalFilename();
                String customFileName = "todolist_image" + "_" + originalFilename;

                Path path = Path.of(imageDirectory + customFileName);
                Files.copy(file.getInputStream(), path);
                todolist.setImagePath(customFileName);
            }
            Todolist createdTodolist = todolistRepository.save(todolist);
            return convertToResponse(createdTodolist);
        } catch (DuplicateDataException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public Optional<TodolistResponse> findById(Long id){
        try{
            return todolistRepository.findById(id).map(this::convertToResponse);
        }catch (Exception e){
            throw new RuntimeException("Failed to find todolist by ID"+e.getMessage(),e);
        }
    }

    public Page<TodolistResponse> findAll(int page, int size){
        try{
            Pageable pageable = PageRequest.of(page,size);
            org.springframework.data.domain.Page<Todolist> todolists = todolistRepository.findAllByDeletedIsNull(pageable);
            return todolists.map(this::convertToResponse);
        }catch (Exception e){
            throw new RuntimeException("Failed to ret"+e.getMessage(),e);
        }
    }

    public TodolistResponse update(Long id, TodolistRequest todolistRequest) {
        try {
            Todolist todolist = todolistRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("todolist with ID" + id +"not found"));
            todolist.setTitle(todolistRequest.getTitle());
            todolist.setDescription(todolistRequest.getDescription());
            User user = userRepository.findByUsername(todolistRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("user not found"));
            todolist.setUser(user);
            Category category = categoryRepository.findById(todolistRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            todolist.setCategory(category);
            todolist.setIsCompleted(todolistRequest.getIsClompleted());
            if(todolistRequest.getImagePath() != null && !todolistRequest.getImagePath().isEmpty()) {
                MultipartFile file = todolistRequest.getImagePath();


                if(file.getSize() > maxFileSize) {
                    throw new RuntimeException("File size exceeds the maximum limit of " + maxFileSize / (1024 * 1024) + "MB");
                }

                String fileType = file.getContentType();
                boolean isValidType = false;
                for (String allowedType : allowedFileTypes) {
                    if (allowedType.equals(fileType)) {
                        isValidType = true;
                        break;
                    }
                }

                if(!isValidType) {
                    throw new RuntimeException("Invalid file type. Only JPEG, PNG, and JPG files are allowed.");
                }

                String originalFilename = file.getOriginalFilename();
                String customFileName = "todolist_image" + "_" + originalFilename;

                Path path = Path.of(imageDirectory + customFileName);
                Files.copy(file.getInputStream(), path);
                todolist.setImagePath(customFileName);
            }            Todolist createdTodolist = todolistRepository.save(todolist);
            return convertToResponse(createdTodolist);

        } catch (DuplicateDataException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    // hard delete
@Transactional
    public void delete(Long id) {
        try {
            if(!todolistRepository.existsById(id)){
                throw new DataNotFoundException("Todolist with ID"+id+" not found");
            }
            todolistRepository.deleteById(id);
        }catch (DataNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("failed to delete todolist"+ e.getMessage(),e);
        }
    }

    // soft delete
    public void softDelete(Long id) {
        try {
            // existsById buat mengecek sebuah data
            if(!todolistRepository.existsById(id)){
                throw new DataNotFoundException("Todolist with ID"+id+" not found");
            }
            //orElseThrow(() -> new DataNotFoundException("Todolist with ID"+id+" not found"))  untuk manampikkan data nya kalau tidak ada
            Todolist todolist = todolistRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Todolist with ID"+id+" not found"));
            // ketika data di deleted akan menampilkan waktu data di hapus.
            todolist.setDeleted(LocalDateTime.now());
            todolistRepository.save(todolist);
        }catch (DataNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("failed to delete todolist"+ e.getMessage(),e);
        }
    }

    public List<TodolistResponse> searchByTitle(String title){
        try{
            return todolistRepository.findByTitleContainingIgnoreCase(title)
                    .stream().map(this::convertToResponse)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException("Failed to search todolist by title" + e.getMessage(),e);
        }
    }

    public  List<TodolistResponse> filterByCategory(Long categoryId){
        try{
            return todolistRepository.findByCategoryId(categoryId)
                    .stream().map(this::convertToResponse)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException("Failed to search todolist by category" + e.getMessage(),e);
        }
    }

    public List<TodolistResponse> findByUserId(UUID userId){
        try {
            return todolistRepository.findByUserId(userId)
                    .stream().map(this::convertToResponse)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException("Failed to search todolist by user id" + e.getMessage(),e);
        }
    }

    private TodolistResponse convertToResponse(Todolist todolist){
        TodolistResponse response = new TodolistResponse();
        response.setId(todolist.getId());
        response.setTitle(todolist.getTitle());
        response.setDescription(todolist.getDescription());
        response.setUsername(todolist.getUser().getUsername());
        response.setCategoryId(todolist.getCategory().getId());
        response.setCategoryName(todolist.getCategory().getName());
        response.setIsCompleted(todolist.getIsCompleted());
        response.setDeleted(todolist.getDeleted());
        response.setCreatedAt(todolist.getCreatedAt());
        response.setUpdatedAt(todolist.getUpdatedAt());
        response.setImagePath(todolist.getImagePath());
        return response;
    }


}
