package com.example.crawlify.utils;

import java.util.HashMap;

public class wordData {
        private String word;
        private HashMap<String, Double> occurrences;
        public wordData(String word,HashMap<String, Double> occurrences){
            this.word=word;
            this.occurrences=occurrences;
        }
}
