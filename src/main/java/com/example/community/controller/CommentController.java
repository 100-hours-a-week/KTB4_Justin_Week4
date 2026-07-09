package com.example.community.controller;

import com.example.community.dto.request.CreateCommentRequest;
import com.example.community.dto.request.UpdateCommentRequest;
import com.example.community.dto.response.CommentResponse;
import com.example.community.global.ApiResponse;
import com.example.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

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
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateCommentRequest requestBody
    ) {
        CommentResponse response = commentService.createComment(postId, userId, requestBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("comment_created_success", response)
        );
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest requestBody
    ) {
        CommentResponse response = commentService.updateComment(postId, commentId, requestBody);

        return ResponseEntity.ok(
                new ApiResponse<>("comment_updated_success", response)
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(postId, commentId);

        return ResponseEntity.noContent().build();
    }
}
