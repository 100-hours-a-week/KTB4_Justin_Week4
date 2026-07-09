package com.example.community.service;

import com.example.community.entity.Post;
import com.example.community.entity.PostLike;
import com.example.community.entity.User;
import com.example.community.exception.AlreadyLikedException;
import com.example.community.exception.LikeNotFoundException;
import com.example.community.exception.PostNotFoundException;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.PostLikeRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new AlreadyLikedException();
        }

        postLikeRepository.save(new PostLike(user, post, LocalDateTime.now()));
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(LikeNotFoundException::new);

        postLikeRepository.delete(postLike);
    }
}
