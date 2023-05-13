package com.example.crawlify.service;

import com.example.crawlify.controller.ResultsResponse;
import com.example.crawlify.model.Page;
import com.example.crawlify.model.SearchResult;
import com.example.crawlify.model.Word;
import java.util.*;

import com.example.crawlify.repository.PageRepository;
import com.example.crawlify.utils.SnippetGenerator;
import javafx.util.Pair;
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
    public ResultsResponse startRanking(List<Word> wordObjectsFromDBList, List<String> queries, int pageNumber){
        sortedPageFinalScore = new ArrayList<>();
        pageTF_IDFScoreHashMap = new HashMap<>();
        calculatePageFinalTF_IDF(wordObjectsFromDBList);
        calculatePageFinalTotalScore();
        sortPagesByFinalScore();
        printPageFinalScore();
        return getSearchResults(queries, pageNumber);
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
        for(Map.Entry<String,Double>pairOfURLAndTF_IDF : pageTF_IDFScoreHashMap.entrySet()){
            pageTF_IDF = pairOfURLAndTF_IDF.getValue();
            page = pageRepository.findByCanonicalUrl(pairOfURLAndTF_IDF.getKey());
            pagePopularity = page.getPopularity();
            pageFinalScore = (pagePopularity*pageTF_IDF)/(pagePopularity+pageTF_IDF);
            Pair<String,Double> pair = new Pair<>(pairOfURLAndTF_IDF.getKey(),pageFinalScore);
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

    private ResultsResponse getSearchResults(List<String> queries, int pageNumber){
        List<SearchResult> searchResults = new ArrayList<>();
        // Assuming pageNumber starts from 1
        int startIndex = (pageNumber - 1) * 10; // The index of the first result to return
        int endIndex = Math.min(startIndex + 10, sortedPageFinalScore.size()); // The index of the last result to return
        for(int i = startIndex; i < endIndex; i++) {
            Pair<String,Double> pair = sortedPageFinalScore.get(i);
            Page resultPage = pageRepository.findByCanonicalUrl(pair.getKey());
            String snippet = SnippetGenerator.generateSnippet(resultPage.getHtml(), queries);
            if(!snippet.isEmpty()){
                searchResults.add(new SearchResult(resultPage.getTitle(), resultPage.getUrl(), snippet));
            }
            else{
                endIndex = Math.min(endIndex + 1, sortedPageFinalScore.size());
            }
        }

        return ResultsResponse.builder().results(searchResults).currentPage(pageNumber).totalPages((int) Math.ceil(sortedPageFinalScore.size() / 10.0)).build();
    }
}