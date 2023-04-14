package com.example.crawlify.controller;

import com.example.crawlify.service.CrawlerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/crawl"})
public class CrawlerController {
    private final CrawlerService crawlerService;

    @Autowired
    public CrawlerController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @PostMapping
    public void startCrawling(@RequestBody CrawlRequest crawlRequest) {
        List<String> seeds = crawlRequest.getSeeds();
        int numThreads = crawlRequest.getNumThreads();
        int maxPagesToCrawl = crawlRequest.getMaxPagesToCrawl();
        crawlerService.setCrawlerThreads(numThreads);
        crawlerService.setMaxPagesToCrawl(maxPagesToCrawl);
        crawlerService.startCrawling(seeds);
    }
}
