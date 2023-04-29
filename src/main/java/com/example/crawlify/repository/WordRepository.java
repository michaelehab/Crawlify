package com.example.crawlify.repository;

import com.example.crawlify.model.Word;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface WordRepository extends MongoRepository<Word, String> {
    Optional<Word> findByword(String word);
}