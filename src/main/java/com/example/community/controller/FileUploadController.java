package com.example.community.controller;

import com.example.community.dto.response.FileUploadResponse;
import com.example.community.global.ApiResponse;
import com.example.community.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @RequestParam("file") MultipartFile file
    ) {
        FileUploadResponse response = fileStorageService.save(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("file_uploaded_success", response)
        );
    }
}