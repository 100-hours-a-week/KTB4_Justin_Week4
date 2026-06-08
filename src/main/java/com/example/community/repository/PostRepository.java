package com.example.community.repository;

import com.example.community.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                "2026-05-12T10:00:00"
        );

        save(post);
    }

    public Long nextId(){
        return idNum++;
    }

    public Post findById(Long postId){
        return posts.get(postId);
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