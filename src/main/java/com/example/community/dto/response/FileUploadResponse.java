package com.example.community.dto.response;

import lombok.Getter;

@Getter
public class FileUploadResponse {

    private final String url;

    public FileUploadResponse(String url) {
        this.url = url;
    }
}
