package com.example.community.controller;

import com.example.community.dto.request.CreateCommentRequest;
import com.example.community.dto.request.DeleteCommentRequest;
import com.example.community.dto.request.UpdateCommentRequest;
import com.example.community.dto.response.CommentResponse;
import com.example.community.entity.Comment;
import com.example.community.global.ApiResponse;
import com.example.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController{

    private final CommentService commentService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long postId
    ) {
        List<CommentResponse> response = commentService.getComments(postId);

        return ResponseEntity.ok(
                new ApiResponse<>("comments_retrieved_success", response)
        );
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse response = commentService.createComment(postId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("comment_created_success", response)
        );
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid
            @RequestBody UpdateCommentRequest request
    ){
        CommentResponse response = commentService.updateComment(postId, commentId, request);

        return ResponseEntity.ok(
                new ApiResponse<>("comment_updated_success", response)
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid
            @RequestBody DeleteCommentRequest request
    ){
        commentService.deleteComment(postId, commentId, request);

        return ResponseEntity.noContent().build();
    }
}