package com.example.community.repository;

import com.example.community.entity.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class PostRepository{

    private final Map<Long, Post> posts = new HashMap<>();
    private Long idNum = 1L;

    public PostRepository(){

        Post post = new Post(
                nextId(),
                "글입니다.",
                "내용입니다.",
                "https://image.kr/img.jpg",
                "Justin",
                LocalDateTime.of(2026, 5, 12, 11, 0),
                LocalDateTime.of(2026, 5, 12, 11, 0)
                );

        save(post);
    }

    public Long nextId(){
        return idNum++;
    }

    public Optional<Post> findById(Long postId){
        return Optional.ofNullable(posts.get(postId));
    }

    public List<Post> findAll(){
        return new ArrayList<>(posts.values());
    }

    public Post save(Post post){
        posts.put(post.getId(), post);
        return post;
    }

    public boolean existsById(Long postId){
        return posts.containsKey(postId);
    }

    public void deleteById(Long postId){
        posts.remove(postId);
    }
}