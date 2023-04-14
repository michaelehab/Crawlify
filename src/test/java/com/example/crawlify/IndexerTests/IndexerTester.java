package com.example.crawlify.IndexerTests;
import com.example.crawlify.Indexer.Indexer;

import java.util.*;

public class IndexerTester implements Runnable {
    private static List <String> htmlFiles=new ArrayList<>();
    private static int noOfThreads;
    public void run(){
        Indexer indexer=new Indexer();
        indexer.run(htmlFiles,noOfThreads);
    }
    public static void main(String args []) {
        htmlFiles.add("src/test/java/com/example/crawlify/IndexerTests/File1.html");
        htmlFiles.add("src/test/java/com/example/crawlify/IndexerTests/File2.html");
        htmlFiles.add("src/test/java/com/example/crawlify/IndexerTests/File3.html");
        Indexer indexer=new Indexer();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter the number of threads:");
        noOfThreads= scanner.nextInt();
        while(noOfThreads<0){
            System.out.println("Please Enter a positive number of threads: ");
            noOfThreads=scanner.nextInt();
        }
        Thread[] threads = new Thread[noOfThreads];

        for (int i = 0; i < noOfThreads; i++) {
            threads[i] = new Thread(new IndexerTester());
            threads[i].setName(Integer.toString(i));
            threads[i].start();
        }
        for (int i = 0; i < noOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        indexer.calculateTF_IDF(htmlFiles.size());
        System.out.println("After calculating TF-IDF: "+indexer.getInvertedIndex());

    }
}
