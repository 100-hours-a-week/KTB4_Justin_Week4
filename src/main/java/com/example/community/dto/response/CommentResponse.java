package com.example.community.dto.response;

import com.example.community.entity.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse{

    private final Long id;
    private final String content;
    private final String author;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private final LocalDateTime updatedAt;

    public CommentResponse(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}