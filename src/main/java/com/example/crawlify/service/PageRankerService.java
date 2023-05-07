package com.example.crawlify.service;

import com.example.crawlify.model.Page;
import com.example.crawlify.model.SearchResult;
import com.example.crawlify.model.Word;
import java.util.*;
import com.example.crawlify.repository.PageRepository;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageRankerService {
    private final PageRepository pageRepository;
    private List<Pair<String,Double>> sortedPageFinalScore;
    private HashMap<String,Double> pageTF_IDFScoreHashMap;
    @Autowired
    public PageRankerService(PageRepository pageRepository){
        this.pageRepository = pageRepository;
    }
    public List<SearchResult> startRanking(List<Word> wordObjectsFromDBList, List<String> queries){
        sortedPageFinalScore=new ArrayList<>();
        pageTF_IDFScoreHashMap=new HashMap<>();
        calculatePageFinalTF_IDF(wordObjectsFromDBList);
        calculatePageFinalTotalScore();
        sortPagesByFinalScore();
        printPageFinalScore();
        return getSearchResults(queries);
    }
    private void calculatePageFinalTF_IDF(List<Word> relevantWords){
        String URL;
        double TF_IDFScore;
        for(Word wordObject:relevantWords){
            for(Map.Entry<String,ArrayList<Double>> TF_IDFAndOccurrences:wordObject.getTF_IDFandOccurrences().entrySet()){
                URL=TF_IDFAndOccurrences.getKey().replace("__",".");
                if(!pageTF_IDFScoreHashMap.containsKey(URL)){
                    pageTF_IDFScoreHashMap.put(URL,TF_IDFAndOccurrences.getValue().get(0));
                }
                else{
                    TF_IDFScore=pageTF_IDFScoreHashMap.get(URL)+TF_IDFAndOccurrences.getValue().get(0);
                    pageTF_IDFScoreHashMap.put(URL,TF_IDFScore);
                }
            }
        }
    }
    private void calculatePageFinalTotalScore(){
        Page page;
        int pagePopularity;
        double pageFinalScore,pageTF_IDF;
        for(Map.Entry<String,Double>pairOfURLAndTF_IDF:pageTF_IDFScoreHashMap.entrySet()){
            pageTF_IDF=pairOfURLAndTF_IDF.getValue();
            page=pageRepository.findByUrl(pairOfURLAndTF_IDF.getKey());
            pagePopularity=page.getPopularity();
            pageFinalScore=(pagePopularity*pageTF_IDF)/(pagePopularity+pageTF_IDF);
            Pair<String,Double> pair=new Pair<>(pairOfURLAndTF_IDF.getKey(),pageFinalScore);
            sortedPageFinalScore.add(pair);
        }
    }
    private void sortPagesByFinalScore(){
        sortedPageFinalScore.sort((p1, p2) -> p2.getValue().compareTo(p1.getValue()));
    }
    private void printPageFinalScore(){
        for(Pair<String,Double> pair:sortedPageFinalScore){

            System.out.println(pair.getKey()+"\t"+pair.getValue());
        }
    }

    private List<SearchResult> getSearchResults(List<String> queries){
        List<SearchResult> searchResults = new ArrayList<>();
        for(Pair<String,Double> pair : sortedPageFinalScore) {
            Page resultPage = pageRepository.findByUrl(pair.getKey());
            String webPage = Jsoup.parse(resultPage.getHtml()).text();

            // Create a string builder to store the modified text
            StringBuilder sb = new StringBuilder();

            // Create a variable to store the maximum snippet size
            int maxSnippetSize = 250;

            // Create a variable to store the index of the first occurrence of any word from the list
            int firstIndex = -1;

            // Loop through each character of the original text
            for (int i = 0; i < webPage.length(); i++) {
                // Get the current character
                char c = webPage.charAt(i);

                // Check if the current character is the start of a word
                if (Character.isLetter(c) && (i == 0 || !Character.isLetter(webPage.charAt(i - 1)))) {
                    // Get the index of the end of the word
                    int j = i + 1;
                    while (j < webPage.length() && Character.isLetter(webPage.charAt(j))) {
                        j++;
                    }

                    // Get the word as a substring
                    String word = webPage.substring(i, j);

                    // Check if the word is in the list of words to make bold
                    if (queries.contains(word)) {
                        // Check if this is the first occurrence of any word from the list
                        if (firstIndex == -1) {
                            // Set the first index to the current index
                            firstIndex = i;
                        }
                    }
                }
            }

            // Check if any word from the list was found
            if (firstIndex != -1) {
                // Create a variable to store the start index of the snippet
                int startIndex = firstIndex - maxSnippetSize / 2;

                // Adjust the start index if it is negative or too close to the end
                if (startIndex < 0) {
                    startIndex = 0;
                } else if (startIndex + maxSnippetSize > webPage.length()) {
                    startIndex = webPage.length() - maxSnippetSize;
                }

                // Create a variable to store the end index of the snippet
                int endIndex = startIndex + maxSnippetSize;

                // Adjust the end index if it is too large
                if (endIndex > webPage.length()) {
                    endIndex = webPage.length();
                }

                // Loop through each character of the snippet
                for (int i = startIndex; i < endIndex; i++) {
                    // Get the current character
                    char c = webPage.charAt(i);

                    // Check if the current character is the start of a word
                    if (Character.isLetter(c) && (i == startIndex || !Character.isLetter(webPage.charAt(i - 1)))) {
                        // Get the index of the end of the word
                        int j = i + 1;
                        while (j < endIndex && Character.isLetter(webPage.charAt(j))) {
                            j++;
                        }

                        // Get the word as a substring
                        String word = webPage.substring(i, j);

                        // Check if the word is in the list of words to make bold
                        if (queries.contains(word)) {
                            // Append the opening <b> tag to the string builder
                            sb.append("<b>");

                            // Append the word to the string builder
                            sb.append(word);

                            // Append the closing </b> tag to the string builder
                            sb.append("</b>");

                            // Update the index to skip the rest of the word
                            i = j - 1;
                        } else {
                            // Append the character to the string builder
                            sb.append(c);
                        }
                    } else {
                        // Append the character to the string builder
                        sb.append(c);
                    }
                }

                searchResults.add(new SearchResult(resultPage.getTitle(), resultPage.getUrl(), sb.toString()));
            }

        }
        return searchResults;
    }
}





// "The Dark Knight"
// "Dark Knight Rises Best Movie IMDB"

// HashMap<String, HashMap<String, List<Double>>>
// List<Word> relevantWordsToQuery;
// List<Word> relevantWordsToPhrase;

// for url in urls:
//      while matcher.find():
//
//      for word in relevantWordsToQuery:
//          if word.TF_IDFandOccurrences.hasKey(url):
//              relevantWordsToPhrase.add(word);
//
//
// pageRanker.startRanking(relevantWordsToPhrase);




