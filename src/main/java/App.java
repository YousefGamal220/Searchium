import DB.MongoDB;
import WebCrawler.WebCrawler;
import WebIndexer.StopWordsRemover;
import WebIndexer.WebIndexer;

import org.bson.Document;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Throwable {
        System.out.println("Searchium Main is called..");

        // Connect to the database
        MongoDB DB = new MongoDB("Searchium");
        DB.checkSeed();

        // Reading the number of threads
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the number of threads you want to run:");
        int threadsNum = in.nextInt();
        in.close();

        // if the entered number of threads is invalid, then make it equal to 1.
        if (threadsNum < 1) {
            System.out.println("Invalid number of threads");
            System.out.println("Running the Crawler in a Single thread");
            threadsNum = 1;
        }

        // Create the crawler and make the threads start crawling
        WebCrawler Crawler = new WebCrawler(DB);

        // Create N threads which will crawl the URLs
        Thread[] crawlers = new Thread[threadsNum];
        for (int i = 0; i < threadsNum; i++) {
            crawlers[i] = new Thread(Crawler);
            crawlers[i].setName("Crawler " + (i + 1));
            crawlers[i].start();
        }

        // Wait until all the threads finish crawling
        for (Thread crawler : crawlers) {
            crawler.join();
        }

        // Asking the user whether he wants to start a new indexing or not
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter 1 for new Indexing or 2 to continue");
        int ans = sc.nextInt();
        boolean update = ans == 2;

        // Get the stopping words from the file
        List<String> stop_words = StopWordsRemover.buildStopWordsCorpus("stopping_words.txt");

        // Create an instance from the Indexer
        WebIndexer webIndexerMain = new WebIndexer(DB, stop_words);

        // Retrieve all the crawled pages from the DB
        Iterator<Document> CrawlerCollectionItr = DB.getAllPages().iterator();

        // Loop through the crawled pages and index them
        int i = 1;
        while (CrawlerCollectionItr.hasNext()) {
            Document d = CrawlerCollectionItr.next();
            String page = d.getString("content");
            String url = d.getString("url");

            System.out.printf("index page: %d url:%s \n", i, url);
            webIndexerMain.runIndexer(page, url);
            i++;
        }

        // Save the indexed words in the DB
        System.out.println("Update Database...");
        webIndexerMain.updateIndexerDB();

        if (update) {
            System.out.println("Update IDF...");
            DB.updateAllIDF();
        }
    }
}