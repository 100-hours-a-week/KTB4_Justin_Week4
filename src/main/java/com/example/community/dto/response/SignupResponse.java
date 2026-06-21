package com.example.community.dto.response;

import lombok.Getter;

@Getter
public class SignupResponse{

    private final Long id;

    public SignupResponse(Long userId) {
        this.id = userId;
    }
}
