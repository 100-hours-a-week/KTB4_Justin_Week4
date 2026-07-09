package com.example.community.service;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostResponse;
import com.example.community.entity.Post;
import com.example.community.entity.PostImage;
import com.example.community.entity.User;
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
import java.util.List;

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
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                user,
                now,
                now
        );

        postRepository.save(post);
        saveImages(post, request.getImageUrls());

        return createPostResponse(post, userId);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPosts() {
        return postRepository.findAll()
                .stream()
                .map(post -> createPostResponse(post, null))
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
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.update(
                request.getTitle(),
                request.getContent(),
                LocalDateTime.now()
        );

        if (request.getImageUrls() != null) {
            replaceImages(post, request.getImageUrls());
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
        List<String> imageUrls = postImageRepository.findAllByPost(post)
                .stream()
                .map(PostImage::getImageUrl)
                .toList();

        boolean liked = false;

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                liked = postLikeRepository.existsByPostAndUser(post, user);
            }
        }

        return new PostResponse(
                post,
                imageUrls,
                postLikeRepository.countByPost(post),
                commentRepository.countByPost(post),
                post.getViewCount(),
                liked
        );
    }

    private void saveImages(Post post, List<String> imageUrls) {
        if (imageUrls == null) {
            return;
        }

        for (String imageUrl : imageUrls) {
            postImageRepository.save(new PostImage(post, imageUrl));
        }
    }

    private void replaceImages(Post post, List<String> imageUrls) {
        postImageRepository.deleteByPost(post);
        saveImages(post, imageUrls);
    }
}
