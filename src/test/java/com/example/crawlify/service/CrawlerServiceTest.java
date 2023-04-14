package com.example.crawlify.service;

import com.example.crawlify.model.Page;
import com.example.crawlify.repository.PageRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CrawlerServiceTest {
    @Mock
    private PageRepository pageRepository;
    @InjectMocks
    private CrawlerService crawlerService;

    public CrawlerServiceTest() {
    }

    @Before
    public void setUp() {
        this.crawlerService = new CrawlerService(pageRepository);
    }

    @Test
    public void testCrawl() {
        List<String> seeds = Arrays.asList("https://www.geeksforgeeks.org/");
        crawlerService.setMaxPagesToCrawl(10);
        crawlerService.setCrawlerThreads(1);
        crawlerService.startCrawling(seeds);
        Mockito.verify(pageRepository, Mockito.times(10)).save(Mockito.any(Page.class));
    }
}
