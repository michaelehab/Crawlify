package com.example.crawlify.controller;

import com.example.crawlify.model.SearchQuery;
import com.example.crawlify.service.SearchQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SearchQueryController {

    private final SearchQueryService searchQueryService;

    @Autowired
    public SearchQueryController(SearchQueryService searchQueryService) {
        this.searchQueryService = searchQueryService;
    }

    public List<SearchQuery> getQueriesByText(String text) {
        //System.out.println("Getting Popular queries for " + text);
        return searchQueryService.findPopularQueriesByText(text);
    }

    public void createQuery(String searchQuery) {
        searchQueryService.saveSearchQuery(searchQuery);
    }
}