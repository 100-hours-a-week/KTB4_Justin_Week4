package com.example.community.dto.response;

import com.example.community.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserResponse{

    private final Long id;
    private final String email;
    private final String nickname;

    @JsonProperty("profile_image")
    private final String profileImage;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getDisplayNickname();
        this.profileImage = user.getProfileImage();
    }
}