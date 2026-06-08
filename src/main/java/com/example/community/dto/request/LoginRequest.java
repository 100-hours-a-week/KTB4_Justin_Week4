package com.example.community.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginRequest{
    private String email;
    private String password;
}
