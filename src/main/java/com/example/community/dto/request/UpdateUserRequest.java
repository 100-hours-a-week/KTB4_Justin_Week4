package com.example.community.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class UpdateUserRequest{

    @NotBlank
    private String nickname;

    @NotBlank
    @JsonProperty("profile_image")
    private String profileImage;
}
