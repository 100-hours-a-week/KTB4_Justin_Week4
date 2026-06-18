package com.example.community.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateUserRequest{

    @NotBlank
    private String nickname;

    @JsonProperty("profile_image")
    private String profileImage;
}
