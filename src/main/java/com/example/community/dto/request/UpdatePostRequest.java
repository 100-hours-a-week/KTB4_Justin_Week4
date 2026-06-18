package com.example.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePostRequest{

    @NotBlank
    private String title;

    @NotBlank
    private String content;
    private String image;
}