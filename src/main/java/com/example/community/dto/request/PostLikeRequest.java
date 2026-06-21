package com.example.community.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostLikeRequest{

    @NotNull
    private Long userId;
}