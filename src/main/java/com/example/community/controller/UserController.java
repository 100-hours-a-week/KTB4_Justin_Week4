package com.example.community.controller;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.dto.response.UpdateUserResponse;
import com.example.community.dto.response.UserResponse;
import com.example.community.global.ApiResponse;
import com.example.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
        List<UserResponse> response = userService.getUsers();

        return ResponseEntity.ok(
                new ApiResponse<>("users_retrieved_success", response)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId) {
        UserResponse response = userService.getUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>("user_retrieved_success", response)
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UpdateUserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>("user_updated_success", response)
        );
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        userService.updatePassword(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>("password_updated_success", null)
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}