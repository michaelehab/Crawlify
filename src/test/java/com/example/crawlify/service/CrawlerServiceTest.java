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
        this.crawlerService = new CrawlerService(this.pageRepository);
    }

    @Test
    public void testCrawl() {
        List<String> seeds = Arrays.asList("https://www.geeksforgeeks.org/");
        this.crawlerService.setMaxPagesToCrawl(10);
        this.crawlerService.setCrawlerThreads(4);
        this.crawlerService.startCrawling(seeds);
        ((PageRepository)Mockito.verify(this.pageRepository, Mockito.times(10))).save((Page)Mockito.any(Page.class));
    }
}
