package com.example.community.dto.request;

import lombok.Getter;

@Getter
public class UpdatePostRequest{
    private String title;
    private String content;
    private String image;
}