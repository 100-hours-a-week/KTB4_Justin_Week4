package com.example.community.service;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.DeletePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.dto.response.PostResponse;
import com.example.community.entity.Post;
import com.example.community.entity.PostImage;
import com.example.community.entity.User;
import com.example.community.exception.PostNotFoundException;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.PostImageRepository;
import com.example.community.repository.PostLikeRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService{

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Post createPost(CreatePostRequest request){
        User user = userRepository.findById(request.getUserId())
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

        return post;
    }

    @Transactional(readOnly = true)
    public List<Post> getPosts(){
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Post getPost(Long postId){
        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public PostResponse toPostResponse(Post post) {
        List<String> imageUrls = postImageRepository.findAllByPost(post).stream()
                .map(PostImage::getImageUrl)
                .toList();

        return new PostResponse(post, imageUrls);
    }

    @Transactional
    public Post updatePost(Long postId, UpdatePostRequest request){
        userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        post.update(
                request.getTitle(),
                request.getContent(),
                LocalDateTime.now()
        );

        if (request.getImageUrls() != null) {
            replaceImages(post, request.getImageUrls());
        }

        return post;
    }

    @Transactional
    public void deletePost(Long postId, DeletePostRequest request){
        userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        postImageRepository.deleteByPost(post);
        postLikeRepository.deleteByPost(post);
        postRepository.deleteById(post.getId());
    }

    private void saveImages(Post post, List<String> imageUrls){
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
