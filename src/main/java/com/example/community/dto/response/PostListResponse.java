package com.example.community.dto.response;

import com.example.community.entity.Genre;
import com.example.community.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {

    private final Long id;
    private final String artist;

    @JsonProperty("track_title")
    private final String trackTitle;

    private final Genre genre;
    private final String author;

    @JsonProperty("author_profile_image")
    private final String authorProfileImage;

    @JsonProperty("image_url")
    private final String imageUrl;

    @JsonProperty("like_count")
    private final long likeCount;

    @JsonProperty("comment_count")
    private final long commentCount;

    private final boolean liked;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    public PostListResponse(Post post, boolean liked) {
        this.id = post.getId();
        this.artist = post.getArtist();
        this.trackTitle = post.getTrackTitle();
        this.genre = post.getGenre();
        this.author = post.getUser().getDisplayNickname();
        this.authorProfileImage = post.getUser().getProfileImage();
        this.imageUrl = post.getImageUrl();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.liked = liked;
        this.createdAt = post.getCreatedAt();
    }
}
