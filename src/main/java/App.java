import DB.MongoDB;
import WebCrawler.WebCrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Throwable {
        System.out.println("Searchium Main is Called");

        // Connect to the database
        MongoDB DB = new MongoDB("Searchium");

        // Creating the list of initial URLs
        Queue<String> URLs = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader("seed.txt"));
        String URL;
        while ((URL = reader.readLine()) != null) URLs.add(URL);
        reader.close();

        // Reading the number of threads
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the number of threads you want to run:");
        int threadsNum = in.nextInt();

        // if the entered number of threads is invalid, then make it equal to the number of URLs in the seed.
        if (threadsNum < 1 || threadsNum > URLs.size())
            threadsNum = URLs.size();
        in.close();
        WebCrawler TheCrawler = new WebCrawler(DB, URLs);
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