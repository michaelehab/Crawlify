package com.example.crawlify.service;

import com.example.crawlify.model.Word;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PhraseSearcherService {
    private final QueryService queryService;

    @Autowired
    public PhraseSearcherService(QueryService queryService) {
        this.queryService = queryService;
    }

    public List<Word> startProcessing(String query) {
        List<Word> relevantWordsToQuery = queryService.startProcessing(query);

        HashMap<String, HashMap<String, List<Double>>> wordIndexesByURL = new HashMap<>();

        for (Word word : relevantWordsToQuery) {
            String wordString = word.getWord();
            HashMap<String, ArrayList<Double>> wordOccurrences = word.getTF_IDFandOccurrences();

            for (Map.Entry<String, ArrayList<Double>> entry : wordOccurrences.entrySet()) {
                String url = entry.getKey();
                ArrayList<Double> tfIdfAndIndexes = entry.getValue();

                if (!wordIndexesByURL.containsKey(url)) {
                    wordIndexesByURL.put(url, new HashMap<>());
                }

                if (!wordIndexesByURL.get(url).containsKey(wordString)) {
                    wordIndexesByURL.get(url).put(wordString, new ArrayList<>());
                }

                List<Double> indexes = tfIdfAndIndexes.subList(1, tfIdfAndIndexes.size());
                wordIndexesByURL.get(url).get(wordString).addAll(indexes);
            }
        }

        // Iterate over the URLs and check if the words appear in order
        for (String url : wordIndexesByURL.keySet()) {
            HashMap<String, List<Double>> wordIndexes = wordIndexesByURL.get(url);
            boolean inOrder = true;
            double prevIndex = -1;
            // Assume the words are sorted by their first occurrence
            for (String word : wordIndexes.keySet()) {
                List<Double> indexes = wordIndexes.get(word);
                double firstIndex = indexes.get(0);
                if (firstIndex < prevIndex) {
                    // The words are not in order
                    inOrder = false;
                    break;
                }
                prevIndex = firstIndex;
            }
            if (!inOrder) {
                // Remove the URL from all words
                for (Word word : relevantWordsToQuery) {
                    HashMap<String, ArrayList<Double>> wordOccurrences = word.getTF_IDFandOccurrences();
                    wordOccurrences.remove(url);
                }
            }
        }

        // Remove any words that have empty TF_IDFandOccurrences hashmap
        Iterator<Word> iterator = relevantWordsToQuery.iterator();
        while (iterator.hasNext()) {
            Word word = iterator.next();
            HashMap<String, ArrayList<Double>> wordOccurrences = word.getTF_IDFandOccurrences();
            if (wordOccurrences.isEmpty()) {
                iterator.remove();
            }
        }

        return relevantWordsToQuery;
    }

    public List<Word> startProcessing(List<String> queries, String operation) {
        // Validate the input parameters
        if (queries == null || queries.isEmpty() || operation == null || (!operation.equals("AND") && !operation.equals("OR") && !operation.equals("NOT"))) {
            return new ArrayList<>();
        }

        // Initialize a list to store the results of each query
        List<List<Word>> queryResults = new ArrayList<>();

        // Iterate over the queries and call the queryService for each one
        for (String query : queries) {
            List<Word> relevantWordsToQuery = this.startProcessing(query);
            queryResults.add(relevantWordsToQuery);
        }

        // Initialize a list to store the final result
        List<Word> finalResult = new ArrayList<>();

        // Perform the operation on the query results
        switch (operation) {
            case "AND" -> {
                // Find the intersection of all query results
                // Assume that the first query result is not empty
                finalResult.addAll(queryResults.get(0));
                // Iterate over the rest of the query results and remove any word that is not in common
                for (int i = 1; i < queryResults.size(); i++) {
                    List<Word> currentResult = queryResults.get(i);
                    finalResult.removeIf(word -> !currentResult.contains(word));
                }
            }
            case "OR" -> {
                // Find the union of all query results
                // Use a set to avoid duplicates
                Set<Word> wordSet = new HashSet<>();
                // Iterate over the query results and add all words to the set
                for (List<Word> currentResult : queryResults) {
                    wordSet.addAll(currentResult);
                }
                // Convert the set to a list
                finalResult.addAll(wordSet);
            }
            case "NOT" -> {
                // Assume that the first query result is not empty
                finalResult.addAll(queryResults.get(0));
                // Iterate over the rest of the query results and remove any word that is in common
                for (int i = 1; i < queryResults.size(); i++) {
                    List<Word> currentResult = queryResults.get(i);
                    finalResult.removeIf(currentResult::contains);
                }
            }
        }

        return finalResult;
    }



}