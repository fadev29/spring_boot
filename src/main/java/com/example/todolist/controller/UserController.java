package com.example.todolist.controller;


import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.service.UserService;
import com.example.todolist.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest){
        try {
            UserResponse userResponse = userService.registerUser(userRequest);
            return ResponseEntity.ok(new ApiResponse<>(200,userResponse));
        }catch (DuplicateDataException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409,e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500,e.getMessage()));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
    try{
        UserResponse userResponse = userService.loginUser(loginRequest);
        String token = jwtUtil.generateToken(userResponse.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(200,token));
    }catch (DuplicateDataException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(409,e.getMessage()));
    } catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500,e.getMessage()));
    }
    }

}
