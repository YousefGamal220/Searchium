import DB.MongoDB;
import WebCrawler.WebCrawler;
import WebIndexer.StopWordsRemover;
import WebIndexer.WebIndexerMain;
import com.mongodb.client.FindIterable;
import org.bson.Document;

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
//        Crawler.checkRobots("https://cu.blackboard.com/?new_loc=%2Fultra%2Fcourses%2F_11843_1%2Fcl%2Foutline");

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

        WebIndexerMain webIndexerMain = new WebIndexerMain(); // Creating Instance from the class
        List<String> stop_words = StopWordsRemover.buildStopWordsCorpus("stopping_words.txt");
        FindIterable<Document> CrawlerCollection = DB.getAllPages();
        while (CrawlerCollection.iterator().hasNext())
        {
            String page = CrawlerCollection.iterator().next().getString("content");
            webIndexerMain.runIndexer(page, stop_words);
            break;
        }
    }
}