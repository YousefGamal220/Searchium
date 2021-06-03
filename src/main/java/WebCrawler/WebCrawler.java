package WebCrawler;

import DB.MongoDB;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

public class WebCrawler implements Runnable {

    // Those should be shared for all threads of the crawler
    public static final int MAX_PAGES_COUNT = 5000;
    public static int counter = 0;
    MongoDB DB;
    Queue<String> URLs;
    PriorityQueue<String> queue = new PriorityQueue<String>();

    /*
     * WebCrawler constructor set's the DB and URL data members
     */
    public WebCrawler(MongoDB DB, Queue<String> URLs) {
        this.DB = DB;
        this.URLs = URLs;
    }

    @Override
    public void run() {
        crawl();
    }

    /*
     * The crawl method starts processing the URLs from the queue
     */
    public void crawl() {
        System.out.println(Thread.currentThread().getName() + ": Started Crawling");

        // while the number of crawled pages is less than the maximum
        while (DB.getPagesCount() < MAX_PAGES_COUNT) {

            // lock the following block of code to make sure that two threads doesn't use the same link
            synchronized (this) {

                // if the URLs queue is not empty then process the first URL in it
                if (URLs.peek() != null) {
                    processPage(URLs.remove());
                }
            }
        }

        System.out.println(Thread.currentThread().getName() + ": Finished Crawling");
    }

    /**
     * Process the page of the given URL
     *
     * @param URL the URL of the page
     */
    public void processPage(String URL) {
        if (URL == null) return;

        // Check if the URL ends with a # to exclude it from the URL
        if (URL.endsWith("#")) {
            URL = URL.substring(0, URL.length() - 1);
        }

        // Check if the URL ends with a / to exclude it from the URL
        if (URL.endsWith("/")) {
            URL = URL.substring(0, URL.length() - 1);
        }

        // Check if the given URL is already in database
        if (DB.getpage(URL).iterator().hasNext()) {
            System.out.println(Thread.currentThread().getName() + ": " + URL + " --> [DUPLICATED]");
            return;
        }

        try {
            // Get the HTML document
            Document doc = Jsoup.connect(URL).ignoreContentType(true).get().clone(); // It may throw an IOException

            // if the number of crawled pages is less than the maximum
            if (DB.getPagesCount() < MAX_PAGES_COUNT) {
                /*
                 * This indicates a problem as the doc used by the MongoDB class is bson
                 * and the one used here is jsoup and it can't be implicitly converted
                 */
                DB.insertpage(counter++, URL, doc.html());

                // Get all the links in the page and add them to the end of the queue
                Elements questions = doc.select("a[href]");
                for (Element link : questions) {
                    if (link.attr("abs:href").contains("http")) {
                        URLs.add(link.attr("abs:href"));
                    }
                }
            }
        } catch (IOException ignored) { // We ignored the catch block as we doesn't want it to do anything with exception
        }
    }
}
