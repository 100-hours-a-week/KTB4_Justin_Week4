package com.example.community.dto.request;

import com.example.community.entity.Genre;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePostRequest{

    @NotBlank
    @Size(max = 100)
    private String artist;

    @NotBlank
    @Size(max = 200)
    @JsonProperty("track_title")
    private String trackTitle;

    @NotBlank
    private String content;

    @NotNull
    private Genre genre;

    @JsonProperty("image_url")
    @Size(max = 500)
    private String imageUrl;
}
