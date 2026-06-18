package com.example.community.dto.response;

import com.example.community.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
public class PostResponse{

    private final Long id;
    private final String title;
    private final String content;
    private final String image;
    private final String author;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private final LocalDateTime updatedAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.author = post.getAuthor();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}