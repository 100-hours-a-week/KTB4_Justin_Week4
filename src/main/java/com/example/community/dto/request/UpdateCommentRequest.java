package com.example.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateCommentRequest{

    @NotBlank
    private String content;
}