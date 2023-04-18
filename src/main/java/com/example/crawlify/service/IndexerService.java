package com.example.crawlify.service;
import com.example.crawlify.model.Page;
import com.example.crawlify.model.Word;
import com.example.crawlify.utils.wordData;
import com.example.crawlify.repository.PageRepository;
import com.example.crawlify.repository.WordRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.englishStemmer;

@Service
public class IndexerService {
    private int numThreads;
    private final WordRepository wordRepository;
    private final PageRepository pageRepository;
    public IndexerService(WordRepository wordRepository,PageRepository pageRepository){
        this.pageRepository=pageRepository;
        this.wordRepository = wordRepository;
    }
    public void setIndexerThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public PageRepository getPageRepository() {
        return pageRepository;
    }

    public void startIndexing(List<Page> pageList){
        IndexerThread indexerThread=new IndexerThread(pageList);
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new IndexerThread(pageList));
            threads[i].setName(Integer.toString(i));
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        indexerThread.calculateTF_IDF(pageList.size());
        //System.out.println("After calculating TF-IDF: "+indexerThread.getInvertedIndex());
    }

    private class IndexerThread implements Runnable {
        private int totalNoWordsInADocument;
        private List<Page> pageList;
        private List<String> words;
        private List<String> stopWords;
        private HashMap<String, Integer> wordFrequency;
        private static HashMap<String, HashMap<String, ArrayList<Double>>> invertedIndex;
        


        @Autowired
        public  IndexerThread(List<Page> pageList) {
            wordFrequency = new HashMap<>();
            invertedIndex = new HashMap<>();
            this.pageList=pageList;
            words = new ArrayList<>();
            stopWords = Arrays.asList(
                    "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in",
                    "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the",
                    "their", "then", "there", "these", "they", "this", "to", "was", "will",
                    "with"
            );
            totalNoWordsInADocument = 0;
        }

        public void calculateTF_IDF(int totalNoOfDocuments) {
            String word, documentName;
            HashMap<String,ArrayList< Double>> innerMap;
            double IDF, TF;
            for (Map.Entry<String, HashMap<String,ArrayList< Double>>> entry : invertedIndex.entrySet()) {
                word = entry.getKey();

                innerMap = entry.getValue();
                IDF = Math.log(totalNoOfDocuments / (double) innerMap.size());
                for (Map.Entry<String, ArrayList<Double>> document : entry.getValue().entrySet()) {
                    documentName = document.getKey();
                    TF = document.getValue().get(0);
                    invertedIndex.get(word).get(documentName).set(0,TF*IDF);

                }
                wordData wordData=new wordData(word,invertedIndex.get(word));
                Word wordInDB=Word.builder().wordData(wordData).build();
                wordRepository.save(wordInDB);

            }


        }

        public void run() {
            int id = Integer.parseInt(Thread.currentThread().getName());
            int start = id * (pageList.size() / numThreads);
            int end = start + (pageList.size() / numThreads);
            if (pageList.size() % numThreads != 0 && id == numThreads - 1) end++;
            String html,URL;
            for (int i = start; i < end; i++) {
                html = pageList.get(i).getHtml();
                if (html == null) return;
                URL=pageList.get(i).getUrl();
                URL=URL.replace(".","__");
                Matcher matcher = createMatcherFromHTML(html);
                processWords(matcher,URL);
                calculateTF(URL);
              //  print();

            }
        }

        private void processWords(Matcher matcher,String URL) {
            double position=0;
            while (matcher.find()) {
                totalNoWordsInADocument++;
                String word = matcher.group().toLowerCase();
                if (removeStopWords(word) != "") {
                    word = wordStemmer(word);
                    calculateWordFrequency(word);
                    addToInvertedIndex(word,URL,position);
                    words.add(word);
                }
                position++;
            }
        }

        private Matcher createMatcherFromHTML(String htmlContent) {
            htmlContent = htmlContent.replaceAll("<[^>]*>", "");
            Pattern pattern = Pattern.compile("\\w+");
            return pattern.matcher(htmlContent);
        }

        private String removeStopWords(String word) {
            if (!stopWords.contains(word))
                return word;
            return "";
        }

        private String wordStemmer(String word) {
            englishStemmer stemmer = new englishStemmer();
            stemmer.setCurrent(word);
            stemmer.stem();
            String stemmedWord = stemmer.getCurrent();
            return stemmedWord;
        }

        private void calculateWordFrequency(String word) {
            if (wordFrequency.containsKey(word))
                wordFrequency.put(word, wordFrequency.get(word) + 1);
            else wordFrequency.put(word, 1);
        }

        private void calculateTF(String URL) {
            double TF;
            for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                TF = (double) entry.getValue() / totalNoWordsInADocument;
                invertedIndex.get(entry.getKey()).get(URL).set(0,TF);
            }
        }

        private void addToInvertedIndex(String word, String URL,double position) {
            synchronized (invertedIndex) {
                if (invertedIndex.containsKey(word)) {
                    if(invertedIndex.get(word).containsKey(URL)){
                        invertedIndex.get(word).get(URL).add(position);
                    }
                    else {
                        ArrayList<Double> tempWordList=new ArrayList<>();
                        tempWordList.add(0.0);
                        tempWordList.add(position);
                        invertedIndex.get(word).put(URL, tempWordList);
                    }
                } else {
                    HashMap<String, ArrayList<Double>> wordInnerMap = new HashMap<>();
                    ArrayList<Double> tempWordList=new ArrayList<>();
                    tempWordList.add(0.0);
                    tempWordList.add(position);
                    wordInnerMap.put(URL, tempWordList);
                    invertedIndex.put(word, wordInnerMap);
                }
            }
        }

        private void print() {
            System.out.println("The current thread is " + Thread.currentThread().getName());
            System.out.println("Words are " + words);
            System.out.println("Word Frequency" + wordFrequency);
            System.out.println("Inverted index " + invertedIndex);
        }

    }

    }


