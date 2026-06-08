package com.example.community.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateUserRequest{
    private String nickname;

    @JsonProperty("profile_image")
    private String profileImage;
}
