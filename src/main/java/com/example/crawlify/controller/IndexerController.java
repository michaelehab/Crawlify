package com.example.crawlify.controller;

import com.example.crawlify.service.IndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/index"})
public class IndexerController {
    private final IndexerService indexerService;

    @Autowired
    public IndexerController(IndexerService indexerService) {
        this.indexerService = indexerService;
    }

    @PostMapping
    public void startIndexing(@RequestBody IndexerRequest indexerRequest) {
        int numThreads = indexerRequest.getNumThreads();
        indexerService.setIndexerThreads(numThreads);
        indexerService.startIndexing();
    }
}
