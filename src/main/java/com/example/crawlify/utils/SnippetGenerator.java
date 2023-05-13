package com.example.crawlify.utils;

import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SnippetGenerator {

    // A private final data member for the snippet size
    private static final int N = 200;

    // A method that takes in a string html and a list of strings words
    // and returns a snippet of size N from the html that has at least one word from the words list and in bold
    public static String generateSnippet(String html, List<String> words) {
        // Create a set of words for faster lookup
        Set<String> wordsSet = new HashSet<>();
        for (String word : words) {
            wordsSet.addAll(List.of(word.toLowerCase().split(" ")));
        }

        // Parse the html string using Jsoup
        Document doc = Jsoup.parse(html);

        // Get the body element from the document
        String string = doc.body().text();

        // Initialize variables to keep track of the start and end indices of the snippet
        int start = 0;
        int end = Math.min(N, string.length());
        int size = string.length(); // store the size of the string

        // Initialize variables to keep track of the best snippet and its word count
        String bestSnippet = "";
        int maxCount = 0;

        // Loop through the string
        while (end <= size) {
            List<String> snippetWordsList = List.of(string.substring(start, end).split(" "));
            // Check if any of the words are in the current snippet
            Set<String> snippetWords = new HashSet<>();

            for (String word : snippetWordsList) {
                snippetWords.add(word.toLowerCase());
            }

            // Get the number of words that match in the current snippet
            int count = 0;
            for (String word : wordsSet) {
                if (snippetWords.contains(word)) {
                    count++;
                }
            }

            // Compare the count with the maximum count so far
            if (count > maxCount) {
                // If the count is higher, update the best snippet and its word count
                maxCount = count;
                bestSnippet = string.substring(start, end);
            }
            // If there are no words in the snippet, move the snippet over by one character
            start++;
            end++;
        }

        // If a snippet is found, bold the matching words and return it
        if (!bestSnippet.isEmpty()) {
            for (String word : wordsSet) {
                String regex = "(?i)" + word; // create a case-insensitive regular expression for the word
                bestSnippet = bestSnippet.replaceAll(regex, // use StringBuilder to concatenate strings
                        "<b>" + word + "</b>" // append strings with StringBuilder // create a case-insensitive regular expression for the word
                ); // replace strings with StringBuilder using replaceAll and regex.
            }
            return bestSnippet;
        }

        // If no snippet is found, return an empty string
        return "";
    }


    // A main method to test the snippet generator
    public static void main(String[] args) {
        // A sample html string
        String html = "<p>This is a paragraph with some <b>bold</b> text and some <i>italic</i> text.</p><p>This is another paragraph with some <u>underlined</u> text and some <s>strikethrough</s> text.</p>";

        // A sample list of words
        List<String> words = Arrays.asList("paragraph", "text", "italic");

        // Call the generateSnippet method and print the result
        String snippet = SnippetGenerator.generateSnippet(html, words);
        System.out.println(snippet);
    }
}
