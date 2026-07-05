package com.example.community.repository;

import com.example.community.entity.Post;
import com.example.community.entity.PostLike;
import com.example.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>{

    boolean existsByPostAndUser(Post post, User user);
    Optional<PostLike> findByPostAndUser(Post post, User user);
    long countByPost(Post post);
    void deleteByPost(Post post);
    void deleteByUser(User user);
}