package com.example.crawlify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class CrawlifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlifyApplication.class, args);
    }

}
