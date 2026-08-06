package com.example.community.dto.response;

import com.example.community.entity.Genre;
import com.example.community.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDetailResponse {

    private final Long id;
    private final String artist;

    @JsonProperty("track_title")
    private final String trackTitle;

    private final Genre genre;
    private final String content;
    private final String author;

    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("author_profile_image")
    private final String authorProfileImage;

    @JsonProperty("image_url")
    private final String imageUrl;

    @JsonProperty("like_count")
    private final long likeCount;

    @JsonProperty("comment_count")
    private final long commentCount;

    @JsonProperty("view_count")
    private final long viewCount;

    private final boolean liked;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private final LocalDateTime updatedAt;

    public PostDetailResponse(Post post, boolean liked) {
        this.id = post.getId();
        this.artist = post.getArtist();
        this.trackTitle = post.getTrackTitle();
        this.genre = post.getGenre();
        this.content = post.getContent();
        this.author = post.getUser().getDisplayNickname();
        this.userId = post.getUser().getId();
        this.authorProfileImage = post.getUser().getProfileImage();
        this.imageUrl = post.getImageUrl();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.viewCount = post.getViewCount();
        this.liked = liked;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
