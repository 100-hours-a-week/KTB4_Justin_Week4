package com.example.community.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginResponse{

    @JsonProperty("user_id")
    private final Long userId;

    private final String nickname;

    @JsonProperty("profile_image")
    private final String profileImage;

    public LoginResponse(Long userId, String nickname, String profileImage){
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}