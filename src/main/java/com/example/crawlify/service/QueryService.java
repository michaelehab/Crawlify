package com.example.crawlify.service;
import com.example.crawlify.model.Word;
import com.example.crawlify.repository.PageRepository;
import com.example.crawlify.repository.WordRepository;
import com.example.crawlify.utils.WordProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QueryService {
    private final WordRepository wordRepository;
    private final PageRepository pageRepository;
    private final WordProcessor wordProcessor;
    private final PageRankerService pageRankerService;
    private String query;
    @Autowired
    public QueryService(WordRepository wordRepository, PageRepository pageRepository, PageRankerService pageRankerService) {
        this.wordRepository = wordRepository;
        this.pageRepository = pageRepository;
        this.pageRankerService = pageRankerService;
        wordProcessor = new WordProcessor();
    }
    public void setQueryToProcess(String query) {
        this.query = query;
    }
    public void startProcessing() {
        List<Word> relevantWords = new ArrayList<>();
        HashMap<String, Integer> queryWordPosition = new HashMap<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(query);
        String word;
        int position = 0;
        while (matcher.find()) {
            word = wordProcessor.changeWordToLowercase(matcher.group());
            if (!Objects.equals(wordProcessor.removeStopWords(word), "")) {
                word = wordProcessor.wordStemmer(word);
                Optional<Word> wordFromDB = wordRepository.findByword(word);
                wordFromDB.ifPresent(relevantWords::add);
                queryWordPosition.put(word, position);
            }
            position++;
        }

        pageRankerService.startRanking(relevantWords);
    }
}
