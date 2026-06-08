package com.example.community.controller;

import com.example.community.dto.request.CreateCommentRequest;
import com.example.community.dto.request.UpdateCommentRequest;
import com.example.community.dto.response.CommentResponse;
import com.example.community.entity.Comment;
import com.example.community.global.ApiResponse;
import com.example.community.service.CommentService;
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
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long postId){
        List<Comment> comments = commentService.getComments(postId);

        if (comments == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        }

        List<CommentResponse> response = comments.stream()
                .map(CommentResponse::new)
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>("comments_retrieved_success", response)
        );
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request
    ){
        Comment comment = commentService.createComment(postId, request);

        if (comment == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("post_not_found", null));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("comment_created_success", new CommentResponse(comment))
        );
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request
    ){
        Comment comment = commentService.updateComment(postId, commentId, request);

        if (comment == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("comment_not_found", null));
        }

        return ResponseEntity.ok(
                new ApiResponse<>("comment_updated_success", new CommentResponse(comment))
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ){
        boolean deleted = commentService.deleteComment(postId, commentId);

        if (!deleted){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}