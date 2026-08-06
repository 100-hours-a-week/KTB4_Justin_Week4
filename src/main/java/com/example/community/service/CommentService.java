package com.example.community.service;

import com.example.community.dto.request.CreateCommentRequest;
import com.example.community.dto.request.UpdateCommentRequest;
import com.example.community.dto.response.CommentResponse;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.exception.AuthenticationRequiredException;
import com.example.community.exception.CommentNotFoundException;
import com.example.community.exception.InvalidRequestException;
import com.example.community.exception.PostNotFoundException;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
        validateAuthenticatedUserId(userId);
        validateCommentContent(request.getContent());
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        User user = findActiveUser(userId);

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
    public CommentResponse updateComment(
            Long postId,
            Long commentId,
            Long userId,
            UpdateCommentRequest request
    ) {
        validateAuthenticatedUserId(userId);
        validatePostExists(postId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getPost().getId().equals(postId)) {
            throw new CommentNotFoundException();
        }
        validateCommentOwner(comment, userId);
        validateCommentContent(request.getContent());

        comment.update(
                request.getContent(),
                LocalDateTime.now()
        );

        return new CommentResponse(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {
        validateAuthenticatedUserId(userId);
        validatePostExists(postId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getPost().getId().equals(postId)) {
            throw new CommentNotFoundException();
        }
        validateCommentOwner(comment, userId);

        commentRepository.delete(comment);
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("access_denied");
        }
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.isWithdrawn()) {
            throw new UserNotFoundException();
        }
        return user;
    }

    private void validateAuthenticatedUserId(Long userId) {
        if (userId == null) {
            throw new AuthenticationRequiredException();
        }
    }

    private void validateCommentContent(String content) {
        if (content == null || content.isBlank()) {
            throw new InvalidRequestException();
        }
    }
}
