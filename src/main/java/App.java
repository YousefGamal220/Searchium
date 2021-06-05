import DB.MongoDB;
import WebCrawler.WebCrawler;
import WebIndexer.WebIndexerMain;
import WebIndexer.StopWordsRemover;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Throwable {
        System.out.println("Searchium Main is Called");

        // Connect to the database
        MongoDB DB = new MongoDB("Searchium");


        // Reading the number of threads
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the number of threads you want to run:");
        int threadsNum = in.nextInt();

        // if the entered number of threads is invalid, then make it equal to the number of URLs in the seed.
        if (threadsNum < 1 || threadsNum > DB.getSeedCount())
            threadsNum = DB.getSeedCount();
        in.close();
        WebCrawler TheCrawler = new WebCrawler(DB);
        Thread[] crawlers = new Thread[threadsNum];
        for (int i = 0; i < threadsNum; i++) {
            crawlers[i] = new Thread(TheCrawler);
            crawlers[i].setName("Crawler " + (i + 1));
            crawlers[i].start();
        }
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