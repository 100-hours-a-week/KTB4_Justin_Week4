package com.example.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Post{

    private Long id;
    private String title;
    private String content;
    private String image;
    private String author;

    private String createdAt;
    private String updatedAt;

    public Post(
            Long id,
            String title,
            String content,
            String image,
            String author,
            String createdAt
    ){
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.author = author;
        this.createdAt = createdAt;
    }

    public void update(
            String title,
            String content,
            String image,
            String updatedAt
    ){
        this.title = title;
        this.content = content;
        this.image = image;
        this.updatedAt = updatedAt;
    }
}