package com.sentinelvault.controller;

import com.sentinelvault.controller.dto.UserRegisterRequest;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterRequest request) {
        User newUserDomain = new User(
                null,
                request.fullName(),
                request.email(),
                request.password(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User registeredUser = userService.registerUser(newUserDomain);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
}