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
import java.util.concurrent.atomic.AtomicInteger;

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
    private final AtomicInteger numOfCrawledPages = new AtomicInteger(0);

    public void setCrawlerThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public void setMaxPagesToCrawl(int maxPagesToCrawl) {
        this.maxPagesToCrawl = maxPagesToCrawl;
    }

    public void startCrawling(List<String> seeds) {
        numOfCrawledPages.set(0);
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
        System.out.println("Successfully Crawled " + numOfCrawledPages.get() + " pages");
    }

    private class CrawlerThread implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if(numOfCrawledPages.get() >= maxPagesToCrawl){
                    break;
                }

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
                        System.out.println("Met page " + canonicalUrl + " again!");
                        Page existingWebpage = pageRepository.findByCanonicalUrl(canonicalUrl);
                        existingWebpage.setPopularity(existingWebpage.getPopularity() + 1);
                        pageRepository.save(existingWebpage);
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

                        Page page = Page.builder().url(url).canonicalUrl(canonicalUrl).title(title).html(html).popularity(1).build();

                        // Check page content
                        String compactString = page.getCompactString();
                        if (visitedPages.putIfAbsent(compactString, true) != null){
                            continue;
                        }

                        pageRepository.save(page);
                        numOfCrawledPages.getAndIncrement();

                        // Enqueue links to visit
                        Elements links = document.select("a[href]");
                        for (Element link : links) {
                            String nextUrl = link.absUrl("href");
                            String normalizedNextUrl = UrlNormalizer.normalize(nextUrl);
                            if (visitedUrls.get(normalizedNextUrl) == null) {
                                urlsToVisit.offer(nextUrl);
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