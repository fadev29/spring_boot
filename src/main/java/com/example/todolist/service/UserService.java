package com.example.todolist.service;

import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.exception.DataNotFoundException;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.model.User;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        try {
            return userRepository.findAll()
                    .stream().map(this::convertToResponse)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user data", e);
        }
    }

    public UserResponse create(UserRequest userRequest) {
        try {
            if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
                throw new DuplicateDataException("Username already exists");
            }

            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());

            if (userRequest.getRole() == null || userRequest.getRole().isEmpty()) {
                userRequest.setRole("USER");
            }
            user.setRole(userRequest.getRole());

            user = userRepository.save(user);
            return convertToResponse(user);
        } catch (DuplicateDataException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public UserResponse update(String username, UserRequest userRequest) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new DataNotFoundException("User with Name " + username+ " not found"));

            // Cek apakah username sudah digunakan oleh user lain
            userRepository.findByUsername(userRequest.getUsername()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(username)) {
                    throw new DuplicateDataException("Username already exists");
                }
            });

            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());

            // Set default role if not provided
            if (userRequest.getRole() == null || userRequest.getRole().isEmpty()) {
                userRequest.setRole("USER");
            }
            user.setRole(userRequest.getRole());

            user = userRepository.save(user);
            return convertToResponse(user);
        } catch (DataNotFoundException | DuplicateDataException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }


    public void delete(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new DataNotFoundException("User with ID " + username+ " not found"));
            userRepository.delete(user);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public Optional<UserResponse> findByname(String username) {
        try {
            return userRepository.findByUsername(username).map(this::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user data: " + e.getMessage());
        }
    }


    private UserResponse convertToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setRole(user.getRole());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
