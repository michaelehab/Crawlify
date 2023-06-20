![Crawlify](https://user-images.githubusercontent.com/29122581/231251576-d5e0b63d-ba09-4ba7-9c5f-806985578363.png)

Crawlify is a simple crawler-based search engine that demonstrates the main features of a search engine (web crawling, indexing and ranking) and the interaction between them. It is implemented using Java, Spring Boot, TypeScript and React JS.

## Features

### Web Crawler:

The crawler visits HTML pages from a list of seed URLs and downloads them to a local directory. It also checks for robots.txt files and normalizes the URLs to avoid duplicates. The crawler is multithreaded and can be configured to use a certain number of threads and limit the number of crawled pages.

### Indexer:

The indexer reads the downloaded HTML files and extracts the terms and their frequencies. It also calculates the document frequency and inverse document frequency for each term. The indexer uses a scoring thread to assign a score to each document based on the term frequencies and other factors. The indexer stores the index in a file for later retrieval.

### Query Processor:

This module receives search queries, performs necessary preprocessing and searches the index for relevant documents. It also supports phrase searching with quotation marks.

### Phrase Searching:

Search engines will generally search for words as phrases when quotation marks are placed around the
phrase. Results obtained when searching for a sentence with quotation marks around them should be a subset of the results obtained when searching for the same sentence without quotation marks.
Note that: results obtained from phrase searching with quotation marks, should return only the webpages
having a sentence with the same order of words.

### Page Ranker:

The ranker module sorts documents based on their popularity and relevance to the search query.

1. Relevance
   Relevance is a relation between the query words and the result page and could be calculated in several
   ways such as tf-idf of the query word in the result page or simply whether the query word appeared in
   the title, heading, or body. And then you aggregate the scores from all query words to produce the final
   page relevance score.
2. Popularity
   Popularity is a measure for the importance of any web page regardless the requested query. You can
   use pagerank algorithm or other ranking algorithms to calculate each page popularity.

### Web Interface:

This module provides a web interface for Crawlify using React JS. It allows the user to enter search queries and see the results with snippets, scores and pagination. It also provides interactive search suggestions based on previous queries.

## Installation

To install Crawlify, you need to have Java 11, Maven, Node.js and Angular CLI installed on your system. You also need to clone this repository using the following command:

```
git clone https://github.com/michaelehab/Crawlify.git
```

Then, you need to build and run the backend using Maven:

```
cd Crawlify
mvn spring-boot:run
```

This will start the backend server on port 8081.

Next, you need to build and run the frontend:

```
cd web
npm install
npm start
```

This will start the frontend server on port 3000.

You can then access the web interface of Crawlify at http://localhost:3000.

## Usage

To use Crawlify via HTTP requests, you need to send POST requests to the following endpoints:

- /crawl: This endpoint starts or resumes the crawling process. It expects a JSON body with three parameters: maxPages (the maximum number of pages to crawl), numThreads (the number of threads to use for crawling), and seeds (an array of seed URLs). For example:

```
{
  "maxPagesToCrawl": 6000,
  "numThreads": 10,
  "seeds": [
    "https://en.wikipedia.org/wiki/Main_Page",
    "https://www.google.com/",
    "https://www.reddit.com/"
  ]
}
```

This endpoint returns a response with a status indicating whether the crawling process started successfully or not.

- /index: This endpoint starts or resumes the indexing process. It expects a JSON body with one parameter: numThreads (the number of threads to use for indexing). For example:

```
{
  "numThreads": 5
}
```

This endpoint returns a response with a message indicating whether the indexing process started successfully or not.

- You can use the search engine by entering a query in the search box and clicking on “Search”. You will see a list of relevant documents ranked by their combined score. You can also see some search suggestions based on your query terms.

## Screenshots
![Crawlify Search](https://github.com/michaelehab/Crawlify/assets/29122581/1377a9bd-22e4-4ee3-b7da-bab6b0119121)
![Crawlify Autocomplete](https://github.com/michaelehab/Crawlify/assets/29122581/ca88691a-a6be-4a7a-89eb-72b2610598df)


