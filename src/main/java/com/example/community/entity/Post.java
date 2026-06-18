package com.example.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Post{

    private Long id;
    private String title;
    private String content;
    private String image;
    private String author;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(
            Long id,
            String title,
            String content,
            String image,
            String author,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(
            String title,
            String content,
            String image,
            LocalDateTime updatedAt
    ){
        this.title = title;
        this.content = content;
        this.image = image;
        this.updatedAt = updatedAt;
    }
}