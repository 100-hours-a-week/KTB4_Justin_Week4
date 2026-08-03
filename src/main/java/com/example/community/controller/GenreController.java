package com.example.community.controller;

import com.example.community.dto.response.GenreResponse;
import com.example.community.entity.Genre;
import com.example.community.global.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getGenres() {
        List<GenreResponse> genres = Arrays.stream(Genre.values())
                .map(GenreResponse::new)
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>("genres_retrieved_success", genres)
        );
    }
}
