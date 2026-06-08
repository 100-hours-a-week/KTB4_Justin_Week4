package com.example.community.controller;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostResponse;
import com.example.community.entity.Post;
import com.example.community.global.ApiResponse;
import com.example.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController{

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPosts(){
        List<Post> posts = postService.getPosts();

        List<PostResponse> response = posts.stream()
                .map(PostResponse::new)
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "posts_retrieved_success",
                        response
                )
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId){
        Post post = postService.getPost(postId);

        if (post == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "post_retrieved_success",
                        new PostResponse(post)
                )
        );
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody CreatePostRequest request
    ){
        Post post = postService.createPost(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        "post_created_success",
                        new PostResponse(post)
                )
        );
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request
    ){
        Post post = postService.updatePost(postId, request);

        if (post == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "post_updated_success",
                        new PostResponse(post)
                )
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId){
        boolean deleted = postService.deletePost(postId);

        if (!deleted){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}