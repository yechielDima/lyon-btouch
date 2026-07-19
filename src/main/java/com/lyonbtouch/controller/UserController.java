package com.lyonbtouch.controller;

import com.lyonbtouch.dto.*;
import com.lyonbtouch.model.User;
import com.lyonbtouch.service.ManagerService;
import com.lyonbtouch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ManagerService managerService;

    public UserController(UserService userService, ManagerService managerService) {
        this.userService = userService;
        this.managerService = managerService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserResponse>> getPendingUsers() {
        List<UserResponse> users = userService.getPendingUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllActiveUsers() {
        List<UserResponse> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<UserResponse> approveUser(@PathVariable Long id,
                                                     @Valid @RequestBody ApproveUserRequest request) {
        UserResponse user = managerService.approveUser(
                id, request.getSystemRole(),
                request.getQualifications(), request.isChecker());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = managerService.updateUserProfile(
                id, request.getSystemRole(),
                request.getQualifications(), request.getIsChecker(), request.getActive());
        return ResponseEntity.ok(user);
    }
}
