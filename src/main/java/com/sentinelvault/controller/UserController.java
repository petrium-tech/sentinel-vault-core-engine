package com.sentinelvault.controller;

import com.sentinelvault.controller.dto.UserRegistrationRequest;
import com.sentinelvault.controller.dto.UserResponse;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.domain.service.UserService;
import com.sentinelvault.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        User userDomain = userMapper.toDomain(request);
        User registeredUser = userService.registerUser(userDomain);
        UserResponse responseBody = userMapper.toResponse(registeredUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}