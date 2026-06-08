package com.example.community.dto.request;

import lombok.Getter;

@Getter
public class CreatePostRequest{
    private String title;
    private String content;
    private String image;

}
