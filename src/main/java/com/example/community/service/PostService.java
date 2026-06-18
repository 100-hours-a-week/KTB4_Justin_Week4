package com.example.community.service;

import com.example.community.dto.request.CreatePostRequest;
import com.example.community.dto.request.UpdatePostRequest;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.exception.PostNotFoundException;
import com.example.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService{

    private final PostRepository postRepository;

    public Post createPost(CreatePostRequest request){
        Post post = new Post(
                postRepository.nextId(),
                request.getTitle(),
                request.getContent(),
                request.getImage(),
                //이후에 인증 토큰을 통해 받아올 예정
                "Justin",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        postRepository.save(post);

        return post;
    }

    public List<Post> getPosts(){
        return postRepository.findAll();
    }

    public Post getPost(Long postId){
        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
    }

    public Post updatePost(Long postId, UpdatePostRequest request){
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getImage(),
                LocalDateTime.now()
        );

        return post;
    }

    public void deletePost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        postRepository.deleteById(post.getId());
    }
}