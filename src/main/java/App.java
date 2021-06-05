import DB.MongoDB;
import WebCrawler.WebCrawler;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Throwable {
        System.out.println("Searchium Main is Called");
        System.out.println("Enter the number of maximum documents to crawl");
        Scanner in = new Scanner(System.in);
        int MAX_PAGES_COUNT = in.nextInt();

        // Connect to the database
        MongoDB DB = new MongoDB("Searchium", MAX_PAGES_COUNT);


        // Reading the number of threads
        System.out.println("Enter the number of threads you want to run:");
        int threadsNum = in.nextInt();

        // if the entered number of threads is invalid, then make it equal to the number of URLs in the seed.
        if (threadsNum < 1) {

            System.out.println("Invalid number of threads");
            System.out.println("Running the Crawler in a Single thread");
            threadsNum = 1;
        }
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


        //WebIndexerMain webIndexerMain = new WebIndexerMain(); // Creating Instance from the class
    }
}