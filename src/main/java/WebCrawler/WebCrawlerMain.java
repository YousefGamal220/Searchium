package WebCrawler;

import DB.MongoDB;

import java.util.Scanner;

public class WebCrawlerMain {
    public static void main(String[] args) throws InterruptedException {
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

        System.out.println("Finished Crawling Successfully!");
    }
}
