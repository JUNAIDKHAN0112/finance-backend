package com.zorvyn.finance.controller;

import com.zorvyn.finance.common.response.ApiResponse;
import com.zorvyn.finance.dto.request.UpdateRoleRequest;
import com.zorvyn.finance.dto.request.UpdateStatusRequest;
import com.zorvyn.finance.dto.response.UserResponse;
import com.zorvyn.finance.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Management", description = "User CRUD and Role Management — Admin Only")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("Users fetched successfully", userService.getAllUsers()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(
                ApiResponse.success("Profile fetched", userService.getMyProfile(auth.getName())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success("User fetched successfully", userService.getUserById(id)));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Role updated successfully", userService.updateRole(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Status updated successfully", userService.updateStatus(id, request)));
    }
}