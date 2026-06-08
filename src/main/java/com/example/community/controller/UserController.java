package com.example.community.controller;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.dto.response.UpdateUserResponse;
import com.example.community.dto.response.UserResponse;
import com.example.community.entity.User;
import com.example.community.global.ApiResponse;
import com.example.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("user_not_found", null));
        }

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
            @RequestBody UpdateUserRequest request
    ){
        User user = userService.updateUser(userId, request);

        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("user_not_found", null));
        }

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
            @RequestBody UpdatePasswordRequest request
    ){
        boolean updated = userService.updatePassword(userId, request);

        if (!updated){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("user_not_found", null));
        }

        return ResponseEntity.ok(
                new ApiResponse<>("password_updated_success", null)
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        boolean deleted = userService.deleteUser(userId);

        if (!deleted){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}