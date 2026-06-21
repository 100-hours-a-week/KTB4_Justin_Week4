package com.example.community.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeleteCommentRequest {

    @NotNull
    private Long userId;
}
