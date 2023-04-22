package com.example.crawlify.controller;
import com.example.crawlify.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping({"/processQuery"})
public class QueryProcessorController {
    private final QueryService queryService;

    @Autowired
    public QueryProcessorController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping
    public void startProcessing(@RequestBody QueryProcessorRequest queryRequest) {
        String Query = queryRequest.getQuery();
        queryService.setQueryToProcess(Query);
        queryService.startProcessing();

    }
}