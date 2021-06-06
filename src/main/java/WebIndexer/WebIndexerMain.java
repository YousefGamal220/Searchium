package WebIndexer;

import DB.MongoDB;
import org.bson.Document;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class WebIndexerMain {
    public static void main(String[] args) throws IOException {
        // Connect to the database
        MongoDB DB = new MongoDB("Searchium");

        // Get the stopping words from the file
        List<String> stop_words = StopWordsRemover.buildStopWordsCorpus("stopping_words.txt");

        // Create an instance from the Indexer
        WebIndexer webIndexerMain = new WebIndexer(DB, stop_words);

        // Retrieve all the crawled pages from the DB
        Iterator<Document> CrawlerCollectionItr = DB.getAllPages().iterator();

        // Loop through the crawled pages and index them
        int i = 1;
        while (CrawlerCollectionItr.hasNext()) {
            Document d = CrawlerCollectionItr.next();
            String page = d.getString("content");
            String url = d.getString("url");

            System.out.printf("index page: %d url:%s \n", i, url);
            webIndexerMain.runIndexer(page, url);
            i++;
        }

        // Save the indexed words in the DB
        webIndexerMain.updateIndexerDB();
    }
}
