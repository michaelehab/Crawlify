![Crawlify](https://user-images.githubusercontent.com/29122581/231251576-d5e0b63d-ba09-4ba7-9c5f-806985578363.png)

## Web Crawler (CrawlerService)
* The crawler checks if a page has already been visited using a visitedPages map that stores the compact string representation of the page as a key to ensure that the same page is not visited more than once.
* The URLs are normalized using the UrlNormalizer class before being visited to ensure that different URLs referring to the same page are treated as the same.
* The crawler only crawls HTML documents as required by the project rules.
* The crawler maintains its state using the visitedUrls and visitedPages maps, as well as the urlsToVisit queue, to ensure that it can be restarted without revisiting previously downloaded documents.
* The crawler checks for robots.txt files using the RobotsChecker class to ensure that the crawler does not visit pages that are disallowed.
* The implementation provides a multithreaded crawler using the ExecutorService and allows the user to control the number of threads using the setCrawlerThreads method.
* The choice of seeds is left to the user to provide as a list of URLs to the startCrawling method.
* The implementation limits the number of crawled pages to 6000 as required by the project.
* The urlsToVisit queue is used to determine the order of page visits.
* The implementation is a standalone program or process and is not integrated with an indexer.