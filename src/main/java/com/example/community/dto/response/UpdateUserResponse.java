package com.example.community.dto.response;

import com.example.community.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateUserResponse{

    private final Long id;
    private final String nickname;

    @JsonProperty("profile_image")
    private final String profileImage;

    public UpdateUserResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
