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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class CrawlerService {
    private final PageRepository pageRepository;
    @Autowired
    public CrawlerService(PageRepository pageRepository){
        this.pageRepository = pageRepository;
    }
    private final Set<String> visitedUrls = new HashSet<>();
    private final Set<String> visitedPages = new HashSet<>();
    private final Queue<String> urlsToVisit = new LinkedList<>();
    private int maxPagesToCrawl;
    private int numThreads;

    public void setCrawlerThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public void setMaxPagesToCrawl(int maxPagesToCrawl) {
        this.maxPagesToCrawl = maxPagesToCrawl;
    }

    public void startCrawling(List<String> seeds) {
        // Add seeds to the queue
        for (String seed : seeds) {
            urlsToVisit.offer(seed);
        }

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

        for (String url : visitedUrls) {
            System.out.println(url);
        }
    }

    private class CrawlerThread implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // Get next URL to visit
                String url = null;
                synchronized (urlsToVisit) {
                    if (!urlsToVisit.isEmpty()) {
                        url = urlsToVisit.poll();
                    }
                }

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
                if (visitedUrls.contains(canonicalUrl)) {
                    continue;
                }

                // Mark URL as visited
                visitedUrls.add(canonicalUrl);

                // Fetch page content
                try {
                    Connection.Response response = Jsoup.connect(url)
                            .followRedirects(false)
                            .execute();
                    int statusCode = response.statusCode();
                    if (statusCode >= 200 && statusCode < 300) {
                        // Check content type
                        String contentType = response.contentType();
                        if (contentType == null || !contentType.startsWith("text/html")) {
                            // Skip non-HTML content
                            continue;
                        }

                        // Parse HTML content
                        Document doc = response.parse();
                        String title = doc.title();
                        String content = doc.body().text();

                        // Save page to database
                        Page page = new Page(url, title, content);
                        String compactString = page.getCompactString();
                        if (visitedPages.contains(compactString)){
                            continue;
                        }
                        visitedPages.add(compactString);
                        pageRepository.save(page);

                        // Enqueue links to visit
                        Elements links = doc.select("a[href]");
                        for (Element link : links) {
                            String nextUrl = link.absUrl("href");
                            String normalizedNextUrl = UrlNormalizer.normalize(nextUrl);
                            if (!visitedUrls.contains(normalizedNextUrl) && (nextUrl.startsWith("http://") || nextUrl.startsWith("https://"))) {
                                synchronized (urlsToVisit) {
                                    if (urlsToVisit.size() < maxPagesToCrawl) {
                                        urlsToVisit.offer(nextUrl);
                                    }
                                }
                            }
                        }
                    } else if (statusCode >= 300 && statusCode < 400) {
                        // Handle redirect by enqueuing the new URL to visit
                        String redirectUrl = response.header("Location");
                        if (!visitedUrls.contains(redirectUrl) && (redirectUrl.startsWith("http://") || redirectUrl.startsWith("https://"))) {
                            synchronized (urlsToVisit) {
                                if (urlsToVisit.size() < maxPagesToCrawl) {
                                    urlsToVisit.offer(redirectUrl);
                                }
                            }
                        }
                    } else {
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