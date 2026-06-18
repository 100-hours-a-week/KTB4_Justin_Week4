package com.example.community.service;

import com.example.community.dto.request.CreateCommentRequest;
import com.example.community.dto.request.UpdateCommentRequest;
import com.example.community.entity.Comment;
import com.example.community.exception.CommentNotFoundException;
import com.example.community.exception.PostNotFoundException;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService{

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<Comment> getComments(Long postId){
        if (!postRepository.existsById(postId)){
            throw new PostNotFoundException();
        }

        return commentRepository.findAllByPostId(postId);
    }

    public Comment createComment(Long postId, CreateCommentRequest request){
        if (!postRepository.existsById(postId)){
            throw new PostNotFoundException();
        }

        Comment comment = new Comment(
                commentRepository.nextId(),
                postId,
                request.getContent(),
                "Justin",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        commentRepository.save(comment);

        return comment;
    }

    public Comment updateComment(Long postId, Long commentId, UpdateCommentRequest request){
        if (!postRepository.existsById(postId)){
            throw new PostNotFoundException();
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getPostId().equals(postId)){
            throw new RuntimeException("comment_not_found");
        }

        comment.update(
                request.getContent(),
                LocalDateTime.now()
        );

        return comment;
    }

    public void deleteComment(Long postId, Long commentId){
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getPostId().equals(postId)) {
            throw new CommentNotFoundException();
        }

        commentRepository.deleteById(commentId);
    }
}