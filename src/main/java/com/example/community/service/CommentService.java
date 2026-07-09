package com.example.community.service;

import com.example.community.dto.request.CreateCommentRequest;
import com.example.community.dto.request.UpdateCommentRequest;
import com.example.community.dto.response.CommentResponse;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.exception.CommentNotFoundException;
import com.example.community.exception.PostNotFoundException;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return commentRepository.findAllByPost(post)
                .stream()
                .map(CommentResponse::new)
                .toList();
    }

    @Transactional
    public CommentResponse createComment(Long postId, Long userId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Comment comment = new Comment(
                post,
                user,
                request.getContent(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        commentRepository.save(comment);

        return new CommentResponse(comment);
    }

    @Transactional
    @PreAuthorize("@commentRepository.findById(#commentId).orElse(null)?.user?.id == authentication.principal")
    public CommentResponse updateComment(Long postId, Long commentId, UpdateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new CommentNotFoundException();
        }

        comment.update(
                request.getContent(),
                LocalDateTime.now()
        );

        return new CommentResponse(comment);
    }

    @Transactional
    @PreAuthorize("@commentRepository.findById(#commentId).orElse(null)?.user?.id == authentication.principal")
    public void deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new CommentNotFoundException();
        }

        commentRepository.delete(comment);
    }
}
