package WebCrawler;

import DB.MongoDB;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebCrawler implements Runnable {

    // Those should be shared for all threads of the crawler
    public static int MAX_PAGES_COUNT;
    public static int counter = 0;
    MongoDB DB;

    /*
     * WebCrawler constructor set's the DB and URL data members
     */
    public WebCrawler(MongoDB DB) {
        this.DB = DB;
        this.MAX_PAGES_COUNT = DB.MAX_PAGES_COUNT;
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


                // if the URLs queue is not empty then process the first URL in it
            String url=DB.popSeed().getString("url");

            System.out.println("Crawler : "+Thread.currentThread().getName() + " will process page : "+url);

            processPage(url);

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
        if (URL.equals(""))return;
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
            System.out.println(Thread.currentThread().getName() + ": " + URL + " --> [DUPLICATED] that entered the seed, will not enter the database");
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
                synchronized (this)
                {
                    counter++;
                }

                DB.insertpage(counter, URL, doc.html());

                // Get all the links in the page and add them to the end of the queue

                if(DB.getSeedCount()+DB.getPagesCount() >= MAX_PAGES_COUNT) return;
                Elements questions = doc.select("a[href]");
                for (Element link : questions) {
                    if (link.attr("abs:href").contains("http")) {
                        // Check if the given URL is already in database
                        String link_url =link.attr("abs:href") ;
                        if (link_url.endsWith("#")) {
                            link_url = link_url.substring(0, link_url.length() - 1);
                        }

                        // Check if the URL ends with a / to exclude it from the URL
                        if (link_url.endsWith("/")) {
                            link_url = link_url.substring(0, link_url.length() - 1);
                        }

                        if (DB.getpage(link_url).iterator().hasNext() || DB.getSeed(link_url).iterator().hasNext() ) {
                            System.out.println(Thread.currentThread().getName() + ": " + link_url + " --> [DUPLICATED] and will not enter the Seed");
                        }
                        else {
                                DB.insertSeed(link_url);
                        }



                    }
                }
            }
        } catch (IOException ignored) { // We ignored the catch block as we doesn't want it to do anything with exception
        }

        System.out.println(Thread.currentThread().getName() + ": " + "finished crawling :" +URL);

    }
}
