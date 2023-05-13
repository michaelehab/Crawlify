package com.example.crawlify.utils;

import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SnippetGenerator {

    // A constant for the maximum length of the snippet
    public static final int snippetSize = 160;

    // A method to generate a snippet given an html body text and a list of strings
    public static String generateSnippet(String html, List<String> words) {

        Document doc = Jsoup.parse(html); //parse the HTML document
        String text = doc.text(); //get the text content
        String[] sentences = text.split("\\.\\s+"); //split into sentences
        StringBuilder snippet = new StringBuilder(); //create a StringBuilder object
        int matchedWords = 0; //keep track of how many words have been matched
        boolean firstSentence = true; //flag to indicate if it is the first sentence

        for (String sentence : sentences) { //loop through the sentences
            for (String word : words) { //loop through the words
                if (sentence.toLowerCase().contains(word.toLowerCase())) { //check if the sentence contains the word
                    matchedWords++; //increment the matched words count
                    break; //break out of the inner loop
                }
            }
            if (matchedWords > 0) { //if the sentence contains any of the words
                if (snippet.length() + sentence.length() + 1 > snippetSize) { //if adding the sentence will exceed the snippet size
                    snippet.setLength(snippetSize - 3); //trim the snippet to fit
                    snippet.append("..."); //add dots at the end
                    break; //break out of the outer loop
                } else { //if adding the sentence will not exceed the snippet size
                    if (!firstSentence) { //if it is not the first sentence
                        snippet.append(". "); //add a dot and a space before adding the sentence
                    }
                    snippet.append(sentence); //add the sentence to the snippet
                }
            }
            firstSentence = false; //set the flag to false after processing the first sentence
        }

        if (!text.startsWith(sentences[0])) { //if the first sentence does not start from the beginning of the text content
            snippet.insert(0, "..."); //add dots at the beginning of the snippet
        }

        for (String word : words) { //loop through the words again
            int start = snippet.indexOf(word);
            int end = start + word.length();
            snippet.replace(start, end, "<b>" + word + "</b>");
        }

        return snippet.toString(); //return the snippet as a String
    }

    // A main method for testing
    public static void main(String[] args) {

        // A sample html body text
        String html = "<html><head><title>Snippet Generator</title></head><body><h1>Snippet Generator</h1><p>This is a java function to generate google like snippets of webpages, given an html body text string of a webpage and a list of strings where each string can have spaces, I want to generate a snippet of length N that has occurences of words from the list, and each word in the list should be bold in the snippet, the snippet may start or end with dots if it's in the middle of the html</p></body></html>";

        // A sample list of strings
        List<String> list = Arrays.asList("java", "snippet", "html", "webpage");

        // Generate and print the snippet
        String snippet = generateSnippet(html, list);
        System.out.println(snippet);
    }
}
