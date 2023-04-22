package com.example.crawlify.service;
import com.example.crawlify.model.Page;
import com.example.crawlify.model.Word;
import com.example.crawlify.repository.PageRepository;
import com.example.crawlify.repository.WordRepository;
import com.example.crawlify.utils.wordProcessor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void startIndexing(){
        List<Page> pageList = pageRepository.findAll();
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
       // System.out.println("get word ten "+wordRepository.findByword("ten"));
    }

    private class IndexerThread implements Runnable {
        private int totalNoWordsInADocument;
        private final List<Page> pageList;
        private final List<String> words;
        private final wordProcessor wordProcessor;

        private final HashMap<String, Integer> wordFrequency;
        private static HashMap<String, HashMap<String, ArrayList<Double>>> invertedIndex;



        @Autowired
        public  IndexerThread(List<Page> pageList) {
            wordFrequency = new HashMap<>();
            invertedIndex = new HashMap<>();
            wordProcessor=new wordProcessor();
            this.pageList=pageList;
            words = new ArrayList<>();
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
                Word wordInDB=Word.builder().word(word).TF_IDFandOccurrences(invertedIndex.get(word)).build();
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
                String word =wordProcessor.changeWordToLowercase(matcher.group());
                if (!Objects.equals(wordProcessor.removeStopWords(word), "")) {
                    word = wordProcessor.wordStemmer(word);
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




        private void calculateWordFrequency(String word) {
            if (wordFrequency.containsKey(word))
                wordFrequency.put(word, wordFrequency.get(word) + 1);
            else wordFrequency.put(word, 1);
        }

        private void calculateTF(String URL) {
            for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                double TF = (double) entry.getValue() / totalNoWordsInADocument;
                if (invertedIndex.get(entry.getKey()) != null) {
                    ArrayList<Double> positionList = invertedIndex.get(entry.getKey()).get(URL);
                    if (positionList != null) {
                        positionList.set(0, TF);
                    }
                }
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


