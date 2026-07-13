package com.lyonbtouch.controller;

import com.lyonbtouch.dto.*;
import com.lyonbtouch.model.User;
import com.lyonbtouch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getFullName(), request.getPhone());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toUserResponse(user));
    }

    @PostMapping("/request-code")
    public ResponseEntity<Map<String, String>> requestCode(@Valid @RequestBody RequestCodeRequest request) {
        userService.requestCode(request.getPhone());
        return ResponseEntity.ok(Map.of("message", "Code sent"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<UserResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        User user = userService.verifyCode(request.getPhone(), request.getCode());
        return ResponseEntity.ok(DtoMapper.toUserResponse(user));
    }
}
