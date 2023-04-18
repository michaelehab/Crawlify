package com.example.crawlify.IndexerTests;
import com.example.crawlify.service.Indexer;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
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
        addToDataBase(indexer.getInvertedIndex());

    }
    private static void addToDataBase(HashMap<String,HashMap<String,Double>> invertedIndex){
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(new ServerAddress("localhost", 27017))))
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

        MongoDatabase database = mongoClient.getDatabase("crawlify");
        MongoCollection<Document> invertedIndexCollection = database.getCollection("invertedIndex");
        Document document = new Document();
        HashMap<String, HashMap<String, Double>> outerMap =invertedIndex;
        for (String outerKey : outerMap.keySet()) {
            Document innerDocument = new Document();
            HashMap<String, Double> innerMap = outerMap.get(outerKey);
            for (String innerKey : innerMap.keySet()) {
                innerDocument.append(innerKey, innerMap.get(innerKey));
            }
            document.append(outerKey, innerDocument);
        }
        invertedIndexCollection.insertOne(document);

    }}
