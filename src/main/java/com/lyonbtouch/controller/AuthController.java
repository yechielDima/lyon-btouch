package com.lyonbtouch.controller;

import com.lyonbtouch.security.JwtService;
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
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
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
        String token = jwtService.generateToken(user.getId(), user.getSystemRole().name());
        
        String cookieHeader = String.format("auth_token=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax", 
                token, 30 * 24 * 60 * 60);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, cookieHeader)
                .body(DtoMapper.toUserResponse(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        String cookieHeader = "auth_token=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax";
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, cookieHeader)
                .body(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@CookieValue(name = "auth_token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            io.jsonwebtoken.Claims claims = jwtService.validateToken(token);
            Long userId = Long.valueOf(claims.getSubject());
            User user = userService.getUser(userId);
            return ResponseEntity.ok(DtoMapper.toUserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
