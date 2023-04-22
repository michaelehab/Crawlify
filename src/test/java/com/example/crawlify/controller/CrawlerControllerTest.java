package com.example.crawlify.controller;

import com.example.crawlify.service.CrawlerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CrawlerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CrawlerService crawlerService;

    public CrawlerControllerTest() {
    }

    @Test
    public void testCrawl() throws Exception {
        CrawlRequest request = new CrawlRequest();
        request.setSeeds(List.of("http://example.com"));
        request.setNumThreads(4);
        request.setMaxPagesToCrawl(1000);
        Mockito.doNothing().when(this.crawlerService).startCrawling(request.getSeeds());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/crawl").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request))).andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(this.crawlerService, Mockito.times(1)).setMaxPagesToCrawl(request.getMaxPagesToCrawl());
        Mockito.verify(this.crawlerService, Mockito.times(1)).setCrawlerThreads(request.getNumThreads());
        Mockito.verify(this.crawlerService, Mockito.times(1)).startCrawling(request.getSeeds());
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }
}
