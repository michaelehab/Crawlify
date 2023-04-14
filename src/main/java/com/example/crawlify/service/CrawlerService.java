package com.example.crawlify.service;

import com.example.crawlify.model.Page;
import com.example.crawlify.repository.PageRepository;
import com.example.crawlify.utils.UrlNormalizer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class CrawlerService {
    private final PageRepository pageRepository;
    @Autowired
    public CrawlerService(PageRepository pageRepository){
        this.pageRepository = pageRepository;
    }
    private final ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> visitedPages = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> urlsToVisit = new ConcurrentLinkedQueue<>();
    private int maxPagesToCrawl;
    private int numThreads;

    public void setCrawlerThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public void setMaxPagesToCrawl(int maxPagesToCrawl) {
        this.maxPagesToCrawl = maxPagesToCrawl;
    }

    public synchronized void savePage(Page page) {
        pageRepository.save(page);
    }

    public void startCrawling(List<String> seeds) {
        // Add seeds to the queue
        urlsToVisit.addAll(seeds);

        // Start crawling threads
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(new CrawlerThread(i));
        }

        // Wait for crawling to finish
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class CrawlerThread implements Runnable {
        private int id;
        public CrawlerThread(int id){
            System.out.println("Crawler Thread with id=" + id + " is now running!");
            this.id = id;
        }
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // Get next URL to visit
                String url = urlsToVisit.poll();

                if (url == null) {
                    // No more URLs to visit
                    break;
                }

                // Check if URL uses http or https protocol
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    continue;
                }

                String canonicalUrl = UrlNormalizer.normalize(url);

                // Check if URL has already been visited
                if (visitedUrls.putIfAbsent(canonicalUrl, true) != null) {
                    continue;
                }

                // Fetch page content
                try {
                    Connection connection = Jsoup.connect(url);
                    Document document = connection.get();
                    if (connection.response().statusCode() == 200) {
                        // Check content type
                        String contentType = connection.response().contentType();
                        if (contentType == null || !contentType.startsWith("text/html")) {
                            // Skip non-HTML content
                            continue;
                        }

                        // Parse HTML content
                        String title = document.title();
                        String content = document.body().text();

                        // Save page to database
                        Page page = new Page(url, title, content);
                        System.out.println("Thread with ID=" + this.id + " is saving page with url=" + url + " now visited urls count is=" + visitedUrls.size());

                        String compactString = page.getCompactString();
                        if (visitedPages.putIfAbsent(compactString, true) != null){
                            continue;
                        }

                        savePage(page);

                        // Enqueue links to visit
                        Elements links = document.select("a[href]");
                        for (Element link : links) {
                            String nextUrl = link.absUrl("href");
                            String normalizedNextUrl = UrlNormalizer.normalize(nextUrl);
                            if (visitedUrls.get(normalizedNextUrl) == null) {
                                if (visitedUrls.size() + urlsToVisit.size() < maxPagesToCrawl) {
                                    urlsToVisit.offer(nextUrl);
                                }
                            }
                        }
                    }
                    else {
                        // Handle other status codes by skipping the link
                        System.err.println("Skipping URL due to status code: " + url);
                    }
                } catch (IOException e) {
                    // Handle connection errors by skipping the link
                    System.err.println("Skipping URL due to connection error: " + url);
                }
            }
        }
    }
}