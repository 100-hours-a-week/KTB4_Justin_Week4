package com.example.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor
public class Post{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String artist;

    @Column(name = "track_title", nullable = false, length = 200)
    private String trackTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Genre genre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;

    public Post(
            String artist,
            String trackTitle,
            String content,
            Genre genre,
            User user,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){
        this.artist = artist;
        this.trackTitle = trackTitle;
        this.content = content;
        this.genre = genre;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public void update(
            String artist,
            String trackTitle,
            String content,
            Genre genre,
            LocalDateTime updatedAt
    ){
        this.artist = artist;
        this.trackTitle = trackTitle;
        this.content = content;
        this.genre = genre;
        this.updatedAt = updatedAt;
    }
}
