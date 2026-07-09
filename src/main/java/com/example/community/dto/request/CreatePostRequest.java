package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class CreatePostRequest{

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @JsonProperty("image_urls")
    private List<String> imageUrls;
}
