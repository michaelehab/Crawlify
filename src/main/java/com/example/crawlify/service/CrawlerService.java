package com.example.crawlify.service;

import com.example.crawlify.model.Page;
import com.example.crawlify.repository.PageRepository;
import com.example.crawlify.utils.RobotsChecker;
import com.example.crawlify.utils.UrlNormalizer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
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

    public void startCrawling(List<String> seeds) {
        System.out.println("Crawler Started with " + numThreads + " Threads");
        // Add seeds to the queue
        urlsToVisit.addAll(seeds);

        // Start crawling threads
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(new CrawlerThread());
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
        @Override
        public synchronized void run() {
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

                // Check if robots are allowed
                if(!RobotsChecker.areRobotsAllowed(url)){
                    continue;
                }

                // Fetch page content
                try {
                    String canonicalUrl = UrlNormalizer.normalize(url);
                    // Check if URL has already been visited
                    if (visitedUrls.putIfAbsent(canonicalUrl, true) != null) {
                        continue;
                    }

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
                        String html = document.body().html();

                        // Save page to database
                        Page page = Page.builder().url(url).title(title).html(html).build();

                        // Check page content
                        String compactString = page.getCompactString();
                        if (visitedPages.putIfAbsent(compactString, true) != null){
                            continue;
                        }

                        pageRepository.save(page);

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
                        System.err.println("Skipping URL due to status code: " + url);
                    }
                } catch (IOException e) {
                    System.err.println("Skipping URL due to connection error: " + url);
                } catch (URISyntaxException e) {
                    System.err.println("Skipping URL due to wrong syntax: " + url);
                }
            }
        }
    }
}