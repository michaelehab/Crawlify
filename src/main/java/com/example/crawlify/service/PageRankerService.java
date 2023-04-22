package com.example.crawlify.service;

import com.example.crawlify.model.Page;
import com.example.crawlify.model.Word;
import java.util.*;
import com.example.crawlify.repository.PageRepository;
import javafx.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class PageRankerService {
    private final PageRepository pageRepository;
    private List<Pair<String,Double>> sortedPageFinalScore;
    private HashMap<String,Double> pageTF_IDFScoreHashMap;
    public PageRankerService(PageRepository pageRepository){
        this.pageRepository=pageRepository;
    }
    public void startRanking(List<Word> wordObjectsFromDBList ){
        sortedPageFinalScore=new ArrayList<>();
        pageTF_IDFScoreHashMap=new HashMap<>();
        calculatePageFinalTF_IDF(wordObjectsFromDBList);
        calculatePageFinalTotalScore();
        sortPagesByFinalScore();
        printPageFinalScore();
    }
    private void calculatePageFinalTF_IDF(List<Word>wordObjectsFromDBList){
        String URL;
        double TF_IDFScore;
        for(Word wordObject:wordObjectsFromDBList){
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
}
