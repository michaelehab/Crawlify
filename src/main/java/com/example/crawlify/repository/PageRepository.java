package com.example.crawlify.repository;

import com.example.crawlify.model.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends MongoRepository<Page, String> {
    Optional<Page> findByCanonicalUrl(String canonicalUrl);
    @Query("{ 'isIndexed' : false }")
    List<Page> findUnindexedPages();
}