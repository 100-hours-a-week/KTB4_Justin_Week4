package com.example.community.controller;

import com.example.community.dto.request.LoginRequest;
import com.example.community.dto.request.SignupRequest;
import com.example.community.dto.response.LoginResponse;
import com.example.community.dto.response.SignupResponse;
import com.example.community.global.ApiResponse;
import com.example.community.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController{

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        "register_success", response)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>("login_success", response)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(){
        authService.logout();

        return ResponseEntity.ok(
                new ApiResponse<>("logout_success", null)
        );
    }
}