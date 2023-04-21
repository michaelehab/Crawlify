package com.example.crawlify.controller;

import java.util.List;

public class IndexerRequest {
    private int numThreads;

    public IndexerRequest() {
    }
    public int getNumThreads() {
        return this.numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

}
