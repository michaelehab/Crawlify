package com.example.crawlify.controller;

import java.util.List;

public class CrawlRequest {
    private List<String> seeds;
    private int numThreads;
    private int maxPagesToCrawl;

    public CrawlRequest() {
    }

    public List<String> getSeeds() {
        return this.seeds;
    }

    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getMaxPagesToCrawl() {
        return this.maxPagesToCrawl;
    }

    public void setMaxPagesToCrawl(int maxPagesToCrawl) {
        this.maxPagesToCrawl = maxPagesToCrawl;
    }
}
