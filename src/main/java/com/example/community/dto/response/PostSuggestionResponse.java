package com.example.community.dto.response;

import com.example.community.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PostSuggestionResponse {

    private final Long id;
    private final String artist;

    @JsonProperty("track_title")
    private final String trackTitle;

    private final String author;

    public PostSuggestionResponse(Post post) {
        this.id = post.getId();
        this.artist = post.getArtist();
        this.trackTitle = post.getTrackTitle();
        this.author = post.getUser().getDisplayNickname();
    }
}
