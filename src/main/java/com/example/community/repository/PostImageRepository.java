package com.example.community.repository;

import com.example.community.entity.Post;
import com.example.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long>{

    List<PostImage> findAllByPost(Post post);
    void deleteByPost(Post post);
}