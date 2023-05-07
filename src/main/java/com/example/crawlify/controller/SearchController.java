package com.example.crawlify.controller;
import com.example.crawlify.model.SearchResult;
import com.example.crawlify.model.Word;
import com.example.crawlify.service.PageRankerService;
import com.example.crawlify.service.PhraseSearcherService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public List<SearchResult> startSearching(@RequestParam("query") String encodedSearchQuery, @RequestParam("page") Integer pageNumber, @RequestParam("ie") String ie) throws UnsupportedEncodingException {
        String searchQuery;
        if (ie.equals("UTF-8")){
            searchQuery = java.net.URLDecoder.decode(encodedSearchQuery, StandardCharsets.UTF_8);
            System.out.println("Decoded query is: " + searchQuery);
        }
        else{
            throw new UnsupportedEncodingException();
        }
        // Create a list to store the strings in the query
        List<String> strings;

        // Create a variable to store the operation in the query
        String operation = "OR";

        String regex = "(.*)\\bAND\\b(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchQuery);

        if (matcher.find()) {
            strings = List.of(matcher.group(1).substring(1, matcher.group(1).length() - 1), matcher.group(2).substring(1, matcher.group(2).length() - 1));
            operation = "AND";
        }
        else {
            regex = "(.*)\\bOR\\b(.*)";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(searchQuery);
            if (matcher.find()) {
                strings = List.of(matcher.group(1).substring(1, matcher.group(1).length() - 1), matcher.group(2).substring(1, matcher.group(2).length() - 1));
                operation = "OR";
            }
            else{
                regex = "(.*)\\bNOT\\b(.*)";
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(searchQuery);
                if (matcher.find()) {
                    strings = List.of(matcher.group(1).substring(1, matcher.group(1).length() - 1), matcher.group(2).substring(1, matcher.group(2).length() - 1));
                    operation = "NOT";
                }
                else{
                    strings = List.of(searchQuery.substring(1, searchQuery.length() - 1).split("\\s+"));
                }
            }
        }

//        for(String s : strings){
//            System.out.println(s);
//        }

        List<Word> relevantWords = phraseSearcherService.startProcessing(strings, operation);
        return pageRankerService.startRanking(relevantWords, strings);
    }
}
