package WebCrawler;

import DB.MongoDB;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class WebCrawler implements Runnable {

    // Those should be shared for all threads of the crawler
    public static int MAX_PAGES_COUNT;
    public static int counter;
    MongoDB DB;

    /*
     * WebCrawler constructor set's the DB and URL data members
     */
    public WebCrawler(MongoDB DB) {
        this.DB = DB;
        System.out.println("Crawled pages: " + DB.getPagesCount());
        counter = DB.getPagesCount();
        MAX_PAGES_COUNT = MongoDB.MAX_PAGES_COUNT;
    }

    @Override
    public void run() {
        crawl();
    }

    /*
     * The crawl method starts processing the URLs from the seed
     */
    public void crawl() {
        // System.out.println(Thread.currentThread().getName() + ": Started Crawling");

        // while the number of crawled pages is less than the maximum
        while (DB.getPagesCount() < MAX_PAGES_COUNT) {
            org.bson.Document doc = DB.popSeed();

            // if there is a link to process
            if (doc != null) {
                String url = doc.getString("url");
                processPage(url);
            } else { // else let the thread wait until there is an available link
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // System.out.println(Thread.currentThread().getName() + ": Finished Crawling");
    }

    public boolean checkRobots(String link) {
        boolean notBlocked = true;

        try {
            // Create a URL instance from the link
            URL url = new URL(link);

            // Build the proper URL to the robots.txt file
            String origin = url.getProtocol() + "://" + url.getHost();
            String robot = origin + "/robots.txt";

            // Create a BufferedReader to read the robots.txt file
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(robot).openStream()));
            String line;

            // read the robots.txt until you find the user-agent line
            while ((line = in.readLine()) != null) {
                if (line.startsWith("User-agent: *"))
                    break;
            }

            while ((line = in.readLine()) != null) {
                if (line.startsWith("Disallow: ") && link.startsWith(origin + line.substring(10))) {
                    notBlocked = false;
                }
                if (line.startsWith("Allow: ") && link.startsWith(origin + line.substring(8))) {
                    notBlocked = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error in parsing robots.txt");
            return false;
        }

        if (!notBlocked) {
            System.out.println(Thread.currentThread().getName() + ": " + link + " --> [Blocked]");
        }

        return notBlocked;
    }

    /**
     * Process the page of the given URL
     *
     * @param URL the URL of the page
     */
    public void processPage(String URL) {
        if (URL == null) return;
        if (URL.equals("")) return;

        try {
            // Get the HTML document
            Document doc = Jsoup.connect(URL).ignoreContentType(true).get().clone(); // It may throw an IOException
            // System.out.println("Crawler : " + Thread.currentThread().getName() + " will process page : " + URL);

            // if the number of crawled pages is less than the maximum
            if (DB.getPagesCount() < MAX_PAGES_COUNT) {
                // Get the title of the page
                String pageTitle = doc.title();

                synchronized (this) {
                    counter++; // Increase the counter of crawled pages
                }

                // Insert the page to the DB
                DB.insertPage(pageTitle, counter, URL, doc.text());

                // Check for the stopping condition
                if (DB.getSeedCount() + DB.getPagesCount() >= MAX_PAGES_COUNT) return;

                // Get all the links in the page
                Elements links = doc.select("a[href]");

                for (Element link : links) {
                    // Check for the stopping condition
                    if (DB.getSeedCount() + DB.getPagesCount() >= MAX_PAGES_COUNT) return;

                    if (link.attr("abs:href").contains("http")) {

                        // Check if the URL contains a # to exclude it from the URL
                        String link_url = link.attr("abs:href");
                        if (link_url.contains("#")) {
                            link_url = link_url.substring(0, link_url.indexOf("#") - 1);
                        }

                        // Check if the URL ends with a / to exclude it from the URL
                        if (link_url.endsWith("/")) {
                            link_url = link_url.substring(0, link_url.length() - 1);
                        }

                        // Check if the link has already been crawled
                        if (DB.getPage(link_url).iterator().hasNext() || DB.getSeed(link_url).iterator().hasNext()) {
                            System.out.println(Thread.currentThread().getName() + ": " + link_url + " --> [DUPLICATED]");
                        }
                        // Check if the link is disallowed in robots.txt
                        else if (checkRobots(link_url)) {
                            DB.insertSeed(link_url);
                            synchronized (this) {
                                this.notifyAll(); // Notify all waiting threads
                            }
                        }
                    }
                }
            }

            // System.out.println(Thread.currentThread().getName() + ": " + "finished crawling :" + URL);

        } catch (IOException e) {
            // We ignored the catch block as we doesn't want it to do anything with exception
            e.printStackTrace();
        }
    }
}
