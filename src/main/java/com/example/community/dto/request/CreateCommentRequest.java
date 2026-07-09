package com.example.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCommentRequest{

    @NotBlank
    private String content;
}