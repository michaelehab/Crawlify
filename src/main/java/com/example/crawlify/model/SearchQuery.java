package com.example.crawlify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "search_query")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuery {
    @Id
    private String id;

    private String query;
    private Long popularity;
}
