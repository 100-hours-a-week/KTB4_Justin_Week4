package com.example.community.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UpdatePasswordRequest{

    @NotBlank
    @JsonProperty("new_password")
    private String newPassword;
}
