package com.example.crawlify.utils;

import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SnippetGenerator {

    // A private final data member for the snippet size
    private static final int N = 200;

    // A method that takes in a string html and a list of strings words
    // and returns a snippet of size N from the html that has at least one word from the words list and in bold
    public static String generateSnippet(String html, List<String> words) {
        // Create a set of words for faster lookup
        Set<String> wordSet = new HashSet<>(words);

        // Parse the html string using Jsoup
        Document doc = Jsoup.parse(html);

        // Get the body element from the document
        Element body = doc.body();

        // Get all the elements from the body element
        Elements elements = body.getAllElements();

        // Initialize the best snippet and its score
        String bestSnippet = "";
        int bestScore = 0;

        // Loop through all possible start and end indices of the snippet
        for (int start = 0; start < elements.size(); start++) {
            for (int end = start; end < elements.size(); end++) {
                // Get the current snippet and its text length
                StringBuilder snippet = new StringBuilder();
                int textLength = 0;
                for (int i = start; i <= end; i++) {
                    Element element = elements.get(i);
                    snippet.append(element.text()).append(" ");
                    textLength += element.text().length() + 1;
                }

                // If the text length is greater than N, break the inner loop
                if (textLength > N) {
                    break;
                }

                // Calculate the score of the current snippet
                int score = 0;
                for (String word : wordSet) {
                    // If the snippet contains the word case insensitively, increase the score by 1
                    if (snippet.toString().toLowerCase().contains(word.toLowerCase())) {
                        score++;
                    }
                }

                // If the score is greater than the best score, update the best snippet and its score
                if (score > bestScore) {
                    bestSnippet = snippet.toString();
                    bestScore = score;
                }
            }
        }

        // If the best snippet is not empty, bold the words that are in the word set case insensitively using <b> and </b> tags
        if (!bestSnippet.isEmpty()) {
            for (String word : wordSet) {
                bestSnippet = bestSnippet.replaceAll("(?i)" + word, "<b>" + word + "</b>");
            }
        }

        // Return the best snippet
        return bestSnippet;
    }

    // A main method to test the snippet generator
    public static void main(String[] args) {
        // A sample html string
        String html = "<p>This is a paragraph with some <b>bold</b> text and some <i>italic</i> text.</p><p>This is another paragraph with some <u>underlined</u> text and some <s>strikethrough</s> text.</p>";

        // A sample list of words
        List<String> words = Arrays.asList("paragraph", "text and", "italic");

        // Call the generateSnippet method and print the result
        String snippet = SnippetGenerator.generateSnippet(html, words);
        System.out.println(snippet);
    }
}
