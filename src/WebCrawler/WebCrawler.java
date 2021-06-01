package WebCrawler;

import DB.MongoDB;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
public class WebCrawler implements Runnable {

    ///Those should be shared for all threads of the crawler
    int counter;
    PriorityQueue<String> queue=new PriorityQueue<String>();




    public MongoDB db;
    public String URL;

    /*
    * WebCrawler constructor set's the db and URL data members
    */
    public WebCrawler(MongoDB db, String URL) {
        this.db = db;
        this.URL = URL;
    }

    @Override
    public void run() {
        try {
            crawl();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /*
    * The crawl method starts processing the initial URL
    */
    public void crawl() throws Throwable {
        System.out.println(Thread.currentThread().getName() + ": Started Crawling");
        processPage(URL);
        System.out.println(Thread.currentThread().getName() + ": Finished Crawling");
    }

    /**
     * Process pages recursively given the initial URL
     *
     * @param URL the URL of the page
     */
    public void processPage(String URL) throws SQLException {
        // Check if the URL ends with a # to exclude it from the URL
        if (URL.endsWith("#")) {
            URL = URL.substring(0, URL.length() - 1);
        }
        // Check if the URL ends with a / to exclude it from the URL
        if (URL.endsWith("/")) {
            URL = URL.substring(0, URL.length() - 1);
        }
        // Check if the given URL is already in database

        org.bson.Document result = this.db.getpage(URL);


        if (result.isEmpty()) {
            // Store the URL to the database to avoid crawling it again
//            query = "INSERT INTO  `Crawler`.`records` " + "(`URL`) VALUES " + "(?);";
//            PreparedStatement statement = db.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
//            statement.setString(1, URL);
//            statement.execute();

            try {
                // Get the HTML document
                Document doc = Jsoup.connect(URL).ignoreContentType(true).get(); // It may throw an IOException



                /*
                * This indicates a problem as the doc used by the MongoDB class is bson
                * and the one used here is jsoup and it can't be implicitly converted
                * */
                /////////////////////db.insertpage(counter , URL , doc);

                System.out.println(URL);

                // Get all the links and recursively call the processPage method
                Elements questions = doc.select("a[href]");
                for (Element link : questions) {
                    if (link.attr("abs:href").contains("http")) {
                        processPage(link.attr("abs:href"));
                    }
                }
            } catch (IOException ignored) { // We ignored the catch block as we doesn't want it to do anything with exception
            }
        }
    }
}
