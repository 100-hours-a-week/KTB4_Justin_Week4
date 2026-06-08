package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class UpdatePasswordRequest{
    @JsonProperty("new_password")
    private String newPassword;
}
