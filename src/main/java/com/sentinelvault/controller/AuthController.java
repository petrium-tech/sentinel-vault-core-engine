package com.sentinelvault.controller;

import com.sentinelvault.controller.dto.UserLoginRequest;
import com.sentinelvault.controller.dto.TokenResponse;
import com.sentinelvault.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UserLoginRequest request) {
        String token = userService.login(request.getEmail(), request.getPassword());
        TokenResponse response = new TokenResponse(token, "Bearer");

        return ResponseEntity.ok(response);
    }
}