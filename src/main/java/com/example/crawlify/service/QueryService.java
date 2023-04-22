package com.example.crawlify.service;
import com.example.crawlify.model.Word;
import com.example.crawlify.repository.PageRepository;
import com.example.crawlify.repository.WordRepository;
import com.example.crawlify.utils.wordProcessor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QueryService {
    private final WordRepository wordRepository;
    private final PageRepository pageRepository;
    private final wordProcessor wordProcessor;
    private String query;

    public QueryService(WordRepository wordRepository,PageRepository pageRepository) {
        this.wordRepository = wordRepository;
        this.pageRepository=pageRepository;
        wordProcessor=new wordProcessor();
    }
    public void setQueryToProcess(String query) {
        this.query = query;
    }
    public void startProcessing() {
        List<Word> wordObjectFromDBList = new ArrayList<>();
        HashMap<String, Integer> queryWordPosition = new HashMap<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(query);
        String word;
        int position = 0;
        List<Word> wordObjectsFromDB;
        while (matcher.find()) {
            word = wordProcessor.changeWordToLowercase(matcher.group());
            if (!Objects.equals(wordProcessor.removeStopWords(word), "")) {
                word = wordProcessor.wordStemmer(word);
                wordObjectsFromDB =wordRepository.findByword(word);
                if(!wordObjectsFromDB.isEmpty())
                    wordObjectFromDBList.add(wordObjectsFromDB.get(0));
                queryWordPosition.put(word, position);
            }
            position++;
        }

        PageRankerService pageRankerService=new PageRankerService(pageRepository);
        pageRankerService.startRanking(wordObjectFromDBList);
    }
}
