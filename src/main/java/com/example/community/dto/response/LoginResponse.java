package com.example.community.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final Long id;
    private final String nickname;

    @JsonProperty("profile_image")
    private final String profileImage;

    @JsonProperty("access_token")
    private final String accessToken;

    public LoginResponse(
            Long userId,
            String nickname,
            String profileImage,
            String accessToken
    ) {
        this.id = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.accessToken = accessToken;
    }
}
