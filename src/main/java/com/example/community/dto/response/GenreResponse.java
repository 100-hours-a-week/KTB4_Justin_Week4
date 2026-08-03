package com.example.community.dto.response;

import com.example.community.entity.Genre;
import lombok.Getter;

@Getter
public class GenreResponse {

    private final String code;
    private final String name;

    public GenreResponse(Genre genre) {
        this.code = genre.name();
        this.name = genre.getDisplayName();
    }
}
