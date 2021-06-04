import DB.MongoDB;
import WebCrawler.WebCrawler;

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


        //WebIndexerMain webIndexerMain = new WebIndexerMain(); // Creating Instance from the class
    }
}