package com.example.community.controller;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.DeletePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostResponse;
import com.example.community.global.ApiResponse;
import com.example.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPosts() {
        List<PostResponse> response = postService.getPosts();

        return ResponseEntity.ok(
                new ApiResponse<>("posts_retrieved_success", response)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable Long postId,
            @RequestParam(name = "user_id", required = false) Long userId
    ) {
        PostResponse response = postService.getPost(postId, userId);

        return ResponseEntity.ok(
                new ApiResponse<>("post_retrieved_success", response)
        );
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @Valid @RequestBody CreatePostRequest request
    ) {
        PostResponse response = postService.createPost(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("post_created_success", response)
        );
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        PostResponse response = postService.updatePost(postId, request);

        return ResponseEntity.ok(
                new ApiResponse<>("post_updated_success", response)
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @Valid @RequestBody DeletePostRequest request
    ) {
        postService.deletePost(postId, request);

        return ResponseEntity.noContent().build();
    }
}