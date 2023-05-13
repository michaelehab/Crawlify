package com.example.crawlify.controller;

import com.example.crawlify.model.SearchResult;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ResultsResponse {
    List<SearchResult> results;
    Double searchTime;
    Integer totalPages;
    Integer currentPage;
}
