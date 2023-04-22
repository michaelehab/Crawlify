package com.example.crawlify.utils;

import org.tartarus.snowball.ext.englishStemmer;

import java.util.Arrays;
import java.util.List;

public class wordProcessor {
    private final List<String> stopWords;
    public wordProcessor(){
        stopWords = Arrays.asList(
                "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in",
                "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the",
                "their", "then", "there", "these", "they", "this", "to", "was", "will",
                "with"
        );
    }
    public String changeWordToLowercase(String word){
        return word.toLowerCase();
    }
    public String removeStopWords(String word) {
        if (!stopWords.contains(word))
            return word;
        return "";
    }
    public String wordStemmer(String word) {
        englishStemmer stemmer = new englishStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
