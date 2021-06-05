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
        System.out.println("Searchium Main is called...");

        // Connect to the database
        MongoDB DB = new MongoDB("Searchium");

        // Reading the number of threads
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the number of threads you want to run:");
        int threadsNum = in.nextInt();
        in.close();

        // if the entered number of threads is invalid, then make it equal to the number of URLs in the seed.
        if (threadsNum < 1 || threadsNum > DB.getSeedCount())
            threadsNum = DB.getSeedCount();

        // Create the crawler and make the threads start crawling
        WebCrawler TheCrawler = new WebCrawler(DB);
        TheCrawler.checkRobots("https://cu.blackboard.com/?new_loc=%2Fultra%2Fcourses%2F_11843_1%2Fcl%2Foutline");
//        Thread[] crawlers = new Thread[threadsNum];
//        for (int i = 0; i < threadsNum; i++) {
//            crawlers[i] = new Thread(TheCrawler);
//            crawlers[i].setName("Crawler " + (i + 1));
//            crawlers[i].start();
//        }
//
//        // Wait until all the threads finish crawling
//        for (Thread crawler : crawlers) {
//            crawler.join();
//        }
//
//        WebIndexerMain webIndexerMain = new WebIndexerMain(); // Creating Instance from the class
//        List<String> stop_words = StopWordsRemover.buildStopWordsCorpus("stopping_words.txt");
//        FindIterable<Document> CrawlerCollection = DB.getAllPages();
//        while (CrawlerCollection.iterator().hasNext()) {
//            String page = CrawlerCollection.iterator().next().getString("content");
//            webIndexerMain.runIndexer(page, stop_words);
//            break;
//        }
    }
}