package com.example.community.controller;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.dto.response.UpdateUserResponse;
import com.example.community.dto.response.UserResponse;
import com.example.community.entity.User;
import com.example.community.global.ApiResponse;
import com.example.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController{

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId){
        User user = userService.getUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "user_retrieved_success",
                        new UserResponse(user)
                )
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid
            @RequestBody UpdateUserRequest request
    ){
        User user = userService.updateUser(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "user_updated_success",
                        new UpdateUserResponse(user)
                )
        );
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long userId,
            @Valid
            @RequestBody UpdatePasswordRequest request
    ){
        userService.updatePassword(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>("password_updated_success", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}