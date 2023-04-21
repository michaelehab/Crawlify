package com.example.crawlify.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class wordData {
        private String word;
        private HashMap<String, ArrayList<Double>> occurrences;
        public wordData(String word,HashMap<String, ArrayList<Double>> occurrences){
            this.word=word;
            this.occurrences=occurrences;
        }
}
