package com.example.crawlify.controller;
import com.example.crawlify.model.SearchResult;
import com.example.crawlify.model.Word;
import com.example.crawlify.service.PageRankerService;
import com.example.crawlify.service.PhraseSearcherService;
import com.example.crawlify.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping({"/search"})
public class SearchController {
    private final PageRankerService pageRankerService;
    private final PhraseSearcherService phraseSearcherService;

    @Autowired
    public SearchController(PageRankerService pageRankerService, PhraseSearcherService phraseSearcherService) {
        this.pageRankerService = pageRankerService;
        this.phraseSearcherService = phraseSearcherService;
    }

    @GetMapping
    public List<SearchResult> startSearching(@RequestParam("query") String searchQuery, @RequestParam("page") Integer pageNumber) {
        // Create a list to store the strings in the query
        List<String> strings = new ArrayList<>();

        // Create a variable to store the operation in the query
        String operation = "OR";

        // Split the query by spaces
        String[] tokens = searchQuery.split("\\s+");

        // Loop through the tokens and check if they are strings or operations
        for (String token : tokens) {
            // If the token starts and ends with double quotes, it is a string
            if (token.startsWith("\"") && token.endsWith("\"")) {
                // Remove the double quotes and add the token to the list of strings
                strings.add(token.substring(1, token.length() - 1));
            }
            // If the token is AND or OR, it is an operation
            else if (token.equals("AND") || token.equals("OR") || token.equals("NOT")) {
                // Set the operation variable to the token
                operation = token;
            }
        }

        List<Word> relevantWords = phraseSearcherService.startProcessing(strings, operation);
        return pageRankerService.startRanking(relevantWords, strings);
    }
}
