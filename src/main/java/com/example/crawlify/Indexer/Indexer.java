package com.example.crawlify.Indexer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tartarus.snowball.ext.englishStemmer;

public class Indexer {
    private int totalNoWordsInADocument;
    private List<String> words;
    private List<String> stopWords;
    private HashMap<String,Integer> wordFrequency;
    private static HashMap<String,HashMap<String,Double>> invertedIndex;

    public Indexer(){
        wordFrequency=new HashMap<>();
        invertedIndex=new HashMap<>();
        words=new ArrayList<>();
        stopWords = Arrays.asList(
                "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in",
                "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the",
                "their", "then", "there", "these", "they", "this", "to", "was", "will",
                "with"
        );
        totalNoWordsInADocument=0;
    }
    public HashMap<String,HashMap<String,Double>> getInvertedIndex(){
        return invertedIndex;
    }
    public void calculateIDF(int totalNoOfDocuments){
        String word,documentName;
        HashMap<String, Double> innerMap;
        double IDF,TF;
        for (Map.Entry<String, HashMap<String, Double>> entry : invertedIndex.entrySet()) {
            word = entry.getKey();
            innerMap = entry.getValue();
            IDF=Math.log(totalNoOfDocuments/(double)innerMap.size());
            for(Map.Entry<String,Double>document:entry.getValue().entrySet()){
                documentName=document.getKey();
                TF=document.getValue();
                invertedIndex.get(word).put(documentName,TF*IDF);
            }
        }
    }

    public void run(List<String> htmlFiles,int noOfThreads) {
        int id=Integer.parseInt(Thread.currentThread().getName());
        int start=id*(htmlFiles.size()/noOfThreads);
        int end=start+(htmlFiles.size()/noOfThreads);
        if(htmlFiles.size()%noOfThreads!=0 && id==noOfThreads-1) end++;
        Document doc;
        for(int i=start;i<end;i++) {
            doc=parseHtmlFile(htmlFiles.get(i));
            if(doc==null) return;
            Matcher matcher =createMatcherFromDocument(doc);
            processWords(matcher);
            calculateTF(htmlFiles.get(i));
            print();

        }
    }
    private void processWords(Matcher matcher){
        while (matcher.find()) {
            totalNoWordsInADocument++;
            String word = matcher.group().toLowerCase();
            if (removeStopWords(word) != "") {
                word = wordStemmer(word);
                calculateWordFrequency(word);
                words.add(word);
            }
        }
    }
    private Matcher createMatcherFromDocument(Document doc){
        StringBuilder stringBuilder = new StringBuilder(doc.toString());
        String htmlContent = stringBuilder.toString();
        htmlContent = htmlContent.replaceAll("<[^>]*>", "");
        Pattern pattern = Pattern.compile("\\w+");
        return pattern.matcher(htmlContent);
    }
    private String removeStopWords(String word){
        if(!stopWords.contains(word))
            return word;
        return "";
    }
    private String wordStemmer(String word){
        englishStemmer stemmer = new englishStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        String stemmedWord = stemmer.getCurrent();
        return stemmedWord;
    }
    private void calculateWordFrequency(String word){
        if(wordFrequency.containsKey(word))
            wordFrequency.put(word, wordFrequency.get(word)+1);
        else wordFrequency.put(word,1);
    }
    private void calculateTF(String htmlFile){
        double TF;
        for(Map.Entry<String,Integer> entry: wordFrequency.entrySet()){
            TF=(double)entry.getValue()/totalNoWordsInADocument;
            addToInvertedIndex(entry.getKey(),htmlFile, TF);
        }
    }
    private void addToInvertedIndex(String word,String htmlFile,double TF){
        synchronized (invertedIndex){
            if(invertedIndex.containsKey(word)){
                invertedIndex.get(word).put(htmlFile,TF);
            }
            else {
                HashMap<String,Double> wordTF=new HashMap<>();
                wordTF.put(htmlFile,TF);
                invertedIndex.put(word,wordTF);
            }
        }
    }
    private void print(){
        System.out.println("The current thread is "+Thread.currentThread().getName());
        System.out.println("Words are "+words);
        System.out.println("Word Frequency"+wordFrequency);
        System.out.println("Inverted index "+invertedIndex);
    }
    private Document parseHtmlFile(String htmlFile){
        Document doc=null;
        try {
            doc = Jsoup.parse(new File(htmlFile), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }



}
