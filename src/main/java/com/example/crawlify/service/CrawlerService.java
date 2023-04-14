package com.example.crawlify.service;

import com.example.crawlify.model.Page;
import com.example.crawlify.repository.PageRepository;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrawlerService {
    private final PageRepository pageRepository;
    private final Set<String> visitedUrls = new HashSet();
    private final Set<String> visitedPages = new HashSet();
    private final Queue<String> urlsToVisit = new LinkedList();
    private int maxPagesToCrawl;
    private int numThreads;

    @Autowired
    public CrawlerService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public void setCrawlerThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public void setMaxPagesToCrawl(int maxPagesToCrawl) {
        this.maxPagesToCrawl = maxPagesToCrawl;
    }

    public void startCrawling(List<String> seeds) {
        Iterator var2 = seeds.iterator();

        while(var2.hasNext()) {
            String seed = (String)var2.next();
            this.urlsToVisit.offer(seed);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(this.numThreads);

        for(int i = 0; i < this.numThreads; ++i) {
            executorService.submit(new CrawlerThread());
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
        }

    }

    private class CrawlerThread implements Runnable {
        private CrawlerThread() {
        }

        public void run() {
            label122:
            while(true) {
                if (!Thread.currentThread().isInterrupted()) {
                    String url = null;
                    synchronized(CrawlerService.this.urlsToVisit) {
                        if (!CrawlerService.this.urlsToVisit.isEmpty()) {
                            url = (String)CrawlerService.this.urlsToVisit.poll();
                        }
                    }

                    if (url != null) {
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            continue;
                        }

                        String canonicalUrl = this.normalizeUrl(url);
                        if (CrawlerService.this.visitedUrls.contains(canonicalUrl)) {
                            continue;
                        }

                        CrawlerService.this.visitedUrls.add(canonicalUrl);

                        try {
                            Connection.Response response = Jsoup.connect(url).followRedirects(false).execute();
                            int statusCode = response.statusCode();
                            String redirectUrl;
                            if (statusCode >= 200 && statusCode < 300) {
                                redirectUrl = response.contentType();
                                if (redirectUrl == null || !redirectUrl.startsWith("text/html")) {
                                    continue;
                                }

                                Document doc = response.parse();
                                String title = doc.title();
                                String content = doc.body().text();
                                Page page = new Page(url, title, content);
                                String compactString = page.getCompactString();
                                if (CrawlerService.this.visitedPages.contains(compactString)) {
                                    continue;
                                }

                                CrawlerService.this.visitedPages.add(compactString);
                                CrawlerService.this.pageRepository.save(page);
                                Elements links = doc.select("a[href]");
                                Iterator var12 = links.iterator();

                                while(true) {
                                    String nextUrl;
                                    String normalizedNextUrl;
                                    do {
                                        do {
                                            if (!var12.hasNext()) {
                                                continue label122;
                                            }

                                            Element link = (Element)var12.next();
                                            nextUrl = link.absUrl("href");
                                            normalizedNextUrl = this.normalizeUrl(nextUrl);
                                        } while(CrawlerService.this.visitedUrls.contains(normalizedNextUrl));
                                    } while(!nextUrl.startsWith("http://") && !nextUrl.startsWith("https://"));

                                    synchronized(CrawlerService.this.urlsToVisit) {
                                        if (CrawlerService.this.urlsToVisit.size() < CrawlerService.this.maxPagesToCrawl) {
                                            CrawlerService.this.urlsToVisit.offer(nextUrl);
                                        }
                                    }
                                }
                            }

                            if (statusCode >= 300 && statusCode < 400) {
                                redirectUrl = response.header("Location");
                                if (CrawlerService.this.visitedUrls.contains(redirectUrl) || !redirectUrl.startsWith("http://") && !redirectUrl.startsWith("https://")) {
                                    continue;
                                }

                                synchronized(CrawlerService.this.urlsToVisit) {
                                    if (CrawlerService.this.urlsToVisit.size() < CrawlerService.this.maxPagesToCrawl) {
                                        CrawlerService.this.urlsToVisit.offer(redirectUrl);
                                    }
                                    continue;
                                }
                            }

                            System.err.println("Skipping URL due to status code: " + url);
                        } catch (IOException var21) {
                            System.err.println("Skipping URL due to connection error: " + url);
                        }
                        continue;
                    }
                }

                return;
            }
        }

        private String normalizeUrl(String url) {
            try {
                URI uri = new URI(url);
                URI normalizedUri = uri.normalize();
                String normalizedUrl = normalizedUri.toString();
                normalizedUrl = normalizedUrl.replace("www.", "").replaceAll("https?://", "");
                int queryIndex = normalizedUrl.indexOf(63);
                if (queryIndex != -1) {
                    normalizedUrl = normalizedUrl.substring(0, queryIndex);
                }

                return normalizedUrl;
            } catch (URISyntaxException var6) {
                throw new RuntimeException(var6);
            }
        }
    }
}