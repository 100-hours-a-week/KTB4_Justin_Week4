package com.example.community.controller;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostResponse;
import com.example.community.dto.response.PageResponse;
import com.example.community.entity.Genre;
import com.example.community.global.ApiResponse;
import com.example.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Genre genre,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        PageResponse<PostResponse> response = postService.getPosts(
                userId,
                page,
                size,
                genre,
                sort
        );

        return ResponseEntity.ok(
                new ApiResponse<>("posts_retrieved_success", response)
        );
    }

    @GetMapping("/liked")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getLikedPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Genre genre
    ) {
        PageResponse<PostResponse> response = postService.getLikedPosts(
                userId,
                page,
                size,
                genre
        );

        return ResponseEntity.ok(
                new ApiResponse<>("liked_posts_retrieved_success", response)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        PostResponse response = postService.getPost(postId, userId);

        return ResponseEntity.ok(
                new ApiResponse<>("post_retrieved_success", response)
        );
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreatePostRequest requestBody
    ) {
        PostResponse response = postService.createPost(userId, requestBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("post_created_success", response)
        );
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdatePostRequest requestBody
    ) {
        PostResponse response = postService.updatePost(postId, userId, requestBody);

        return ResponseEntity.ok(
                new ApiResponse<>("post_updated_success", response)
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        postService.deletePost(postId, userId);

        return ResponseEntity.noContent().build();
    }
}
