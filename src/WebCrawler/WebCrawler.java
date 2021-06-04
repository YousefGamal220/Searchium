package WebCrawler;

import DB.DB;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler implements Runnable {
    public DB db;
    public String URL;

    /*
    * WebCrawler constructor set's the db and URL data members
    */
    public WebCrawler(DB db, String URL) {
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
        db.modifyQuery("TRUNCATE records;");
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
        String query = "SELECT * FROM records WHERE URL='" + URL + "';";
        ResultSet result = db.selectQuery(query);

        if (!result.next()) {
            // Store the URL to the database to avoid crawling it again
            query = "INSERT INTO  `Crawler`.`records` " + "(`URL`) VALUES " + "(?);";
            PreparedStatement statement = db.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, URL);
            statement.execute();

            try {
                // Get the HTML document
                Document doc = Jsoup.connect(URL).ignoreContentType(true).get(); // It may throw an IOException

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
