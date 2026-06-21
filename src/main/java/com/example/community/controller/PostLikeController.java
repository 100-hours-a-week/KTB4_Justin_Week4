package com.example.community.controller;

import com.example.community.dto.request.PostLikeRequest;
import com.example.community.global.ApiResponse;
import com.example.community.service.PostLikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostLikeRequest request
    ) {
        postLikeService.likePost(postId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("like_created_success", null));
    }

    @DeleteMapping("")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostLikeRequest request
    ) {
        postLikeService.unlikePost(postId, request);

        return ResponseEntity.noContent().build();
    }
}
