package com.example.crawlify.service;

import com.example.crawlify.model.Page;
import com.example.crawlify.repository.PageRepository;
import java.util.List;
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

    @Test
    public void testCrawl() {
        List<String> seeds = List.of("https://www.github.com/", "https://www.khanacademy.org/", "https://www.programiz.com/", "https://www.tutorialspoint.com/");
        crawlerService.setMaxPagesToCrawl(20);
        crawlerService.setCrawlerThreads(4);
        crawlerService.startCrawling(seeds);
        Mockito.verify(pageRepository, Mockito.times(20)).save(Mockito.any(Page.class));
    }
}
