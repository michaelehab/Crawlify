package com.example.crawlify.controller;
import com.example.crawlify.model.Word;
import com.example.crawlify.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping({"/processQuery"})
public class QueryProcessorController {
    private final QueryService queryService;

    @Autowired
    public QueryProcessorController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public void startProcessing(@RequestBody QueryProcessorRequest queryRequest) {
        String query = queryRequest.getQuery();
        List<Word> relevantWordsToQuery = queryService.startProcessing(query);
    }
}
