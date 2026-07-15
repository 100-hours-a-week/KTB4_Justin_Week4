package com.example.community.service;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostResponse;
import com.example.community.entity.Post;
import com.example.community.entity.PostImage;
import com.example.community.entity.User;
import com.example.community.exception.AuthenticationRequiredException;
import com.example.community.exception.InvalidRequestException;
import com.example.community.exception.PostNotFoundException;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostImageRepository;
import com.example.community.repository.PostLikeRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        validateAuthenticatedUserId(userId);
        validatePostValues(request.getTitle(), request.getContent(), request.getImageUrl());
        User user = findActiveUser(userId);

        LocalDateTime now = LocalDateTime.now();

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                user,
                now,
                now
        );

        postRepository.save(post);
        saveImage(post, request.getImageUrl());

        return createPostResponse(post, userId);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPosts(Long userId) {
        Set<Long> likedPostIds = userId == null
                ? Set.of()
                : postLikeRepository.findLikedPostIdsByUserId(userId);

        return postRepository.findAll()
                .stream()
                .map(post -> createPostResponse(post, likedPostIds.contains(post.getId())))
                .toList();
    }

    @Transactional
    public PostResponse getPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.increaseViewCount();

        return createPostResponse(post, userId);
    }

    @Transactional
    @PreAuthorize("@postRepository.findById(#postId).orElse(null)?.user?.id == authentication.principal")
    public PostResponse updatePost(Long postId, Long userId, UpdatePostRequest request) {
        validateAuthenticatedUserId(userId);
        validatePostValues(request.getTitle(), request.getContent(), request.getImageUrl());
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.update(
                request.getTitle(),
                request.getContent(),
                LocalDateTime.now()
        );

        if (request.getImageUrl() != null) {
            replaceImage(post, request.getImageUrl());
        }

        return createPostResponse(post, userId);
    }

    @Transactional
    @PreAuthorize("@postRepository.findById(#postId).orElse(null)?.user?.id == authentication.principal")
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        postRepository.delete(post);
    }

    private PostResponse createPostResponse(Post post, Long userId) {
        String imageUrl = postImageRepository.findByPost(post)
                .map(PostImage::getImageUrl)
                .orElse(null);

        boolean liked = false;

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                liked = postLikeRepository.existsByPostAndUser(post, user);
            }
        }

        return createPostResponse(post, imageUrl, liked);
    }

    private PostResponse createPostResponse(Post post, boolean liked) {
        String imageUrl = postImageRepository.findByPost(post)
                .map(PostImage::getImageUrl)
                .orElse(null);

        return createPostResponse(post, imageUrl, liked);
    }

    private PostResponse createPostResponse(Post post, String imageUrl, boolean liked) {
        return new PostResponse(
                post,
                imageUrl,
                postLikeRepository.countByPost(post),
                commentRepository.countByPost(post),
                post.getViewCount(),
                liked
        );
    }

    private void saveImage(Post post, String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }
        postImageRepository.save(new PostImage(post, imageUrl));
    }

    private void replaceImage(Post post, String imageUrl) {
        postImageRepository.deleteByPost(post);
        postImageRepository.flush();
        saveImage(post, imageUrl);
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

    private void validatePostValues(String title, String content, String imageUrl) {
        if (title == null || title.isBlank() || title.length() > 100
                || content == null || content.isBlank()) {
            throw new InvalidRequestException();
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }
        if (!isValidImageUrl(imageUrl)) {
            throw new InvalidRequestException();
        }
    }

    private boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(imageUrl);
            return ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
