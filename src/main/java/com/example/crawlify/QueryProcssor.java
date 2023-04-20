package com.example.crawlify;
import java.util.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import org.tartarus.snowball.ext.englishStemmer;

import java.io.IOException;

/*steps
1- tokenize:split query to words
2- stemming

 */


public class QueryProcssor {

    public QueryProcssor()
    {

    }
    public static void processQuery(String query, Set<String> links,Set<String> words) throws IOException {
        //tokenization
        englishStemmer  stemmer=new englishStemmer();
        String[] tokens = query.split("\\W+");//expression to split query to words
        List<String> arrayStemmedTokens = new ArrayList<String>();
        for (String token : tokens) {
            stemmer.setCurrent(token);
            if (stemmer.stem()) {
                arrayStemmedTokens.add(stemmer.getCurrent());
            }
        }
        words.addAll(arrayStemmedTokens);
        //connect to mongodb
        String connectionString = "mongodb://localhost:27017/crawlify";
        var mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("crawlify");
        MongoCollection<Document> collection = database.getCollection("Word");
// get links
        for (String word : words) {
            Document found = (Document) collection.find(new Document("_id", word)).first();
            if (found != null) {
                ArrayList<Document> arrlinks = (ArrayList<Document>) found.get("Links");
                for (Document link : arrlinks) {
                    String temp = (String) link.get("Link");
                    links.add(temp);
                }
            }
        }


    }
    //main for testing
    public static void main(String[] args) throws IOException {

        System.out.println("What do want to search about .....: ");
        String query;
        Scanner scan =new Scanner(System.in);
        query=scan.nextLine();
        scan.close();
        // words after stemming
        Set<String> arrayWords = new HashSet<>();

        // links where words are mentioned
        Set<String> arrayLink = new  HashSet<>();

        processQuery(query,arrayLink,arrayWords);
        System.out.println();
        for (String word: arrayWords) {
            System.out.println(word);
        }
        for (String link: arrayLink) {
            System.out.println(link);
        }

    }
}

