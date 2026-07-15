package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdatePostRequest{

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;

    @JsonProperty("image_url")
    @Size(max = 500)
    private String imageUrl;
}
