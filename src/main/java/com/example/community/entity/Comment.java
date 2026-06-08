package com.example.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Comment{

    private Long id;
    private Long postId;
    private String content;
    private String author;
    private String createdAt;
    private String updatedAt;

    public Comment(Long id, Long postId, String content, String author, String createdAt) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    public void update(String content, String updatedAt) {
        this.content = content;
        this.updatedAt = updatedAt;
    }
}