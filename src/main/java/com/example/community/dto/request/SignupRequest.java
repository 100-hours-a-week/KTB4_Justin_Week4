package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SignupRequest{
    private String email;
    private String password;
    private String nickname;
    @JsonProperty("profile_image")
    private String profileImage;
}
