package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeletePostRequest {

    @NotNull
    @JsonProperty("user_id")
    private Long userId;
}
