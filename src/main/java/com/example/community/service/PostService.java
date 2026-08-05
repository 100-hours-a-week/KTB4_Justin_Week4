package com.example.community.service;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostResponse;
import com.example.community.dto.response.PageResponse;
import com.example.community.entity.Genre;
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
import com.example.community.repository.PostCountProjection;
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
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
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
                now,
                now
        );

        postRepository.save(post);
        saveImage(post, request.getImageUrl());

        return createSinglePostResponse(post, userId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> getPosts(
            Long userId,
            int page,
            int size,
            Genre genre,
            String sort
    ) {
        validatePageRequest(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );
        Page<Post> postPage;
        boolean popular = "popular".equalsIgnoreCase(sort);

        if (popular) {
            Pageable unsortedPageable = PageRequest.of(page, size);
            postPage = genre == null
                    ? postRepository.findAllOrderByLikeCount(unsortedPageable)
                    : postRepository.findByGenreOrderByLikeCount(genre, unsortedPageable);
        } else if (sort == null || "latest".equalsIgnoreCase(sort)) {
            postPage = genre == null
                    ? postRepository.findAll(pageable)
                    : postRepository.findByGenre(genre, pageable);
        } else {
            throw new InvalidRequestException();
        }

        // Popular ranking aggregates all likes first. Joining users into that aggregation
        // makes MySQL join 50,000 authors before LIMIT. Load only the page authors instead.
        if (popular && !postPage.isEmpty()) {
            postRepository.findAllWithUserByIdIn(
                    postPage.getContent().stream().map(Post::getId).toList()
            );
        }

        List<PostResponse> content = createPostResponses(postPage.getContent(), userId, false);

        return new PageResponse<>(postPage, content);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> getLikedPosts(
            Long userId,
            int page,
            int size,
            Genre genre
    ) {
        validateAuthenticatedUserId(userId);
        validatePageRequest(page, size);

        Page<Post> postPage = postLikeRepository.findLikedPosts(
                userId,
                genre,
                PageRequest.of(page, size)
        );
        List<PostResponse> content = createPostResponses(postPage.getContent(), userId, true);

        return new PageResponse<>(postPage, content);
    }

    @Transactional
    public PostResponse getPost(Long postId, Long userId) {
        Post post = postRepository.findDetailById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.increaseViewCount();

        return createSinglePostResponse(post, userId);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long userId, UpdatePostRequest request) {
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
            replaceImage(post, request.getImageUrl());
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

    private PostResponse createSinglePostResponse(Post post, Long userId) {
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

        return new PostResponse(
                post,
                imageUrl,
                postLikeRepository.countByPost(post),
                commentRepository.countByPost(post),
                post.getViewCount(),
                liked
        );
    }

    private List<PostResponse> createPostResponses(
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

        Map<Long, String> imageUrls = postImageRepository.findAllByPostIdIn(postIds)
                .stream()
                .collect(Collectors.toMap(
                        postImage -> postImage.getPost().getId(),
                        PostImage::getImageUrl
                ));
        Map<Long, Long> likeCounts = toCountMap(postLikeRepository.countByPostIds(postIds));
        Map<Long, Long> commentCounts = toCountMap(commentRepository.countByPostIds(postIds));

        Set<Long> likedPostIds;
        if (allLiked) {
            likedPostIds = Set.copyOf(postIds);
        } else if (userId == null) {
            likedPostIds = Set.of();
        } else {
            likedPostIds = postLikeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);
        }

        return posts.stream()
                .map(post -> new PostResponse(
                        post,
                        imageUrls.get(post.getId()),
                        likeCounts.getOrDefault(post.getId(), 0L),
                        commentCounts.getOrDefault(post.getId(), 0L),
                        post.getViewCount(),
                        likedPostIds.contains(post.getId())
                ))
                .toList();
    }

    private Map<Long, Long> toCountMap(Collection<PostCountProjection> counts) {
        return counts.stream()
                .collect(Collectors.toMap(
                        PostCountProjection::getPostId,
                        PostCountProjection::getCount
                ));
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
