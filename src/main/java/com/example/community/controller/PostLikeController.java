package com.example.community.controller;

import com.example.community.global.ApiResponse;
import com.example.community.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        postLikeService.likePost(postId, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("like_created_success", null));
    }

    @DeleteMapping("")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        postLikeService.unlikePost(postId, userId);

        return ResponseEntity.noContent().build();
    }
}
