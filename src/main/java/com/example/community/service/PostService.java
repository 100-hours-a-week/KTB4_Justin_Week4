package com.example.community.service;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostDetailResponse;
import com.example.community.dto.response.PostListResponse;
import com.example.community.dto.response.PageResponse;
import com.example.community.entity.Genre;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.exception.AuthenticationRequiredException;
import com.example.community.exception.InvalidRequestException;
import com.example.community.exception.PostNotFoundException;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.PostLikeRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostDetailResponse createPost(Long userId, CreatePostRequest request) {
        validateAuthenticatedUserId(userId);
        validatePostValues(request.getArtist(), request.getTrackTitle(), request.getContent(), request.getImageUrl());
        User user = findActiveUser(userId);
        Genre genre = request.getGenre();

        LocalDateTime now = LocalDateTime.now();

        Post post = new Post(
                request.getArtist(),
                request.getTrackTitle(),
                request.getContent(),
                genre,
                user,
                request.getImageUrl(),
                now,
                now
        );

        postRepository.save(post);

        return createSinglePostResponse(post, userId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostListResponse> getPosts(
            Long userId,
            int page,
            int size,
            Genre genre,
            String sort,
            String keyword
    ) {
        validatePageRequest(page, size);

        boolean popular = "popular".equalsIgnoreCase(sort);
        boolean latest = sort == null || "latest".equalsIgnoreCase(sort);

        if (!popular && !latest) {
            throw new InvalidRequestException();
        }

        Sort postSort = popular
                ? Sort.by(Sort.Direction.DESC, "likeCount")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt"))
                        .and(Sort.by(Sort.Direction.DESC, "id"))
                : Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page, size, postSort);
        Page<Post> postPage;

        if (keyword != null && !keyword.isBlank()) {
            postPage = postRepository.searchPosts(
                    keyword.trim(),
                    genre,
                    pageable
            );
        } else {
            postPage = genre == null
                    ? postRepository.findAll(pageable)
                    : postRepository.findByGenre(genre, pageable);
        }
        List<PostListResponse> content = createPostResponses(postPage.getContent(), userId, false);

        return new PageResponse<>(postPage, content);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostListResponse> getLikedPosts(
            Long userId,
            int page,
            int size,
            Genre genre,
            String keyword
    ) {
        validateAuthenticatedUserId(userId);
        validatePageRequest(page, size);

        String normalizedKeyword = keyword == null || keyword.isBlank()
                ? null
                : keyword.trim();

        Page<Post> postPage = postLikeRepository.findLikedPosts(
                userId,
                genre,
                normalizedKeyword,
                PageRequest.of(page, size)
        );
        List<PostListResponse> content = createPostResponses(postPage.getContent(), userId, true);

        return new PageResponse<>(postPage, content);
    }

    @Transactional
    public PostDetailResponse getPost(Long postId, Long userId) {
        if (postRepository.incrementViewCount(postId) == 0) {
            throw new PostNotFoundException();
        }

        Post post = postRepository.findDetailById(postId)
                .orElseThrow(PostNotFoundException::new);

        return createSinglePostResponse(post, userId);
    }

    @Transactional
    public PostDetailResponse updatePost(Long postId, Long userId, UpdatePostRequest request) {
        validateAuthenticatedUserId(userId);
        Post post = postRepository.findDetailById(postId)
                .orElseThrow(PostNotFoundException::new);
        validatePostOwner(post, userId);
        validatePostValues(request.getArtist(), request.getTrackTitle(), request.getContent(), request.getImageUrl());
        Genre genre = request.getGenre();

        post.update(
                request.getArtist(),
                request.getTrackTitle(),
                request.getContent(),
                genre,
                LocalDateTime.now()
        );

        if (request.getImageUrl() != null) {
            post.updateImage(request.getImageUrl());
        }

        return createSinglePostResponse(post, userId);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        validateAuthenticatedUserId(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        validatePostOwner(post, userId);

        postRepository.delete(post);
    }

    private void validatePostOwner(Post post, Long userId) {
        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("access_denied");
        }
    }

    private PostDetailResponse createSinglePostResponse(Post post, Long userId) {
        boolean liked = userId != null
                && postLikeRepository.existsByPostIdAndUserId(post.getId(), userId);

        return new PostDetailResponse(post, liked);
    }

    private List<PostListResponse> createPostResponses(
            List<Post> posts,
            Long userId,
            boolean allLiked
    ) {
        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        Set<Long> likedPostIds;
        if (allLiked) {
            likedPostIds = Set.copyOf(postIds);
        } else if (userId == null) {
            likedPostIds = Set.of();
        } else {
            likedPostIds = postLikeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);
        }

        return posts.stream()
                .map(post -> new PostListResponse(
                        post,
                        likedPostIds.contains(post.getId())
                ))
                .toList();
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

    private void validatePageRequest(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidRequestException();
        }
    }

    private void validatePostValues(String artist, String trackTitle, String content, String imageUrl) {
        if (artist == null || artist.isBlank() || artist.length() > 100
                || trackTitle == null || trackTitle.isBlank() || trackTitle.length() > 200
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

        String uploadPrefix = "/uploads/";
        if (imageUrl.startsWith(uploadPrefix)) {
            String fileName = imageUrl.substring(uploadPrefix.length());

            return !fileName.isBlank()
                    && !fileName.contains("..")
                    && !fileName.contains("/")
                    && !fileName.contains("\\");
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
