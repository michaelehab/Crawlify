package com.example.crawlify.repository;

import com.example.crawlify.model.SearchQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface SearchQueryRepository extends MongoRepository<SearchQuery, String> {
    Optional<SearchQuery> findSearchQueryByQuery(String query);
    @Query(value = "{'query': {$regex : ?0, $options: 'i'}}")
    Page<SearchQuery> findByQueryTextRegex(String regexString, Pageable pageable);
}
