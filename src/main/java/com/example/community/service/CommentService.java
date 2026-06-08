package com.example.community.service;

import com.example.community.dto.request.CreateCommentRequest;
import com.example.community.dto.request.UpdateCommentRequest;
import com.example.community.entity.Comment;
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
            return null;
        }

        return commentRepository.findAllByPostId(postId);
    }

    public Comment createComment(Long postId, CreateCommentRequest request){
        if (!postRepository.existsById(postId)){
            return null;
        }

        Comment comment = new Comment(
                commentRepository.nextId(),
                postId,
                request.getContent(),
                "Justin",
                LocalDateTime.now().toString()
        );

        commentRepository.save(comment);

        return comment;
    }

    public Comment updateComment(Long postId, Long commentId, UpdateCommentRequest request){
        if (!postRepository.existsById(postId)){
            return null;
        }

        Comment comment = commentRepository.findById(commentId);

        if (comment == null){
            return null;
        }

        if (!comment.getPostId().equals(postId)){
            return null;
        }

        comment.update(
                request.getContent(),
                LocalDateTime.now().toString()
        );

        return comment;
    }

    public boolean deleteComment(Long postId, Long commentId){
        if (!postRepository.existsById(postId)){
            return false;
        }

        Comment comment = commentRepository.findById(commentId);

        if (comment == null){
            return false;
        }

        if (!comment.getPostId().equals(postId)){
            return false;
        }

        commentRepository.deleteById(commentId);

        return true;
    }
}