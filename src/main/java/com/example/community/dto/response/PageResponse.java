package com.example.community.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<T> {

    private final List<T> content;

    @JsonProperty("current_page")
    private final int currentPage;

    @JsonProperty("total_pages")
    private final int totalPages;

    @JsonProperty("total_elements")
    private final long totalElements;

    @JsonProperty("page_size")
    private final int pageSize;

    @JsonProperty("has_next")
    private final boolean hasNext;

    @JsonProperty("has_previous")
    private final boolean hasPrevious;

    public PageResponse(Page<?> page, List<T> content) {
        this.content = content;
        this.currentPage = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageSize = page.getSize();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }
}
