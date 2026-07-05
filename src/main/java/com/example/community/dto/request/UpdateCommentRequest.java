package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateCommentRequest{

    @NotNull
    @JsonProperty("user_id")
    private Long userId;

    @NotBlank
    private String content;
}