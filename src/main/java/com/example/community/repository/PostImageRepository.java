package com.example.community.repository;

import com.example.community.entity.Post;
import com.example.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long>{

    Optional<PostImage> findByPost(Post post);
    List<PostImage> findAllByPostIdIn(Collection<Long> postIds);
    void deleteByPost(Post post);
}
