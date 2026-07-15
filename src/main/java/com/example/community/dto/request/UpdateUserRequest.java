package com.example.community.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class UpdateUserRequest{

    @NotBlank
    @Size(max = 10)
    @Pattern(regexp = "^\\S+$")
    private String nickname;

    @NotBlank
    @JsonProperty("profile_image")
    private String profileImage;
}
