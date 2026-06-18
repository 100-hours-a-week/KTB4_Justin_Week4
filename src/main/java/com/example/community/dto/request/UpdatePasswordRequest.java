package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class UpdatePasswordRequest{

    @NotBlank
    @JsonProperty("new_password")
    private String newPassword;
}
