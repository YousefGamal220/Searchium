import DB.MongoDB;
import WebCrawler.WebCrawler;
import WebIndexer.StopWordsRemover;
import WebIndexer.WebIndexerMain;
import org.bson.Document;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Throwable {
        System.out.println("Searchium Main is called..");

        // Reading the maximum pages to crawl
        System.out.println("Enter the number of maximum documents to crawl");
        Scanner in = new Scanner(System.in);
        int MAX_PAGES_COUNT = in.nextInt();

        // Connect to the database
        MongoDB DB = new MongoDB("Searchium", MAX_PAGES_COUNT);

        // Reading the number of threads
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

        List<String> stop_words = StopWordsRemover.buildStopWordsCorpus("stopping_words.txt");
        WebIndexerMain webIndexerMain = new WebIndexerMain(DB, stop_words); // Creating Instance from the class

        Iterator<Document> CrawlerCollectionItr = DB.getAllPages().iterator();
        int i = 1;
        while (CrawlerCollectionItr.hasNext()) {
            Document d = CrawlerCollectionItr.next();
            String page = d.getString("content");
            String url = d.getString("url");

            System.out.printf("index page: %d url:%s \n", i, url);
            webIndexerMain.runIndexer(page, url);
            i++;
        }

        webIndexerMain.updateIDF();
    }
}