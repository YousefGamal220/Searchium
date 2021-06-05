package DB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MongoDB {
    public static final int MAX_PAGES_COUNT = 500;
    private MongoCollection<Document> CrawlerCollection;
    private MongoCollection<Document> IndexerCollection;
    private MongoCollection<Document> SeedCollection;

    public MongoDB(String Database) {
        try {
            // Create the DB server connection string
            ConnectionString connString = new ConnectionString("mongodb://localhost:27017/?readPreference=primary&appname=MongoDB%20Compass&ssl=false");

            // Build the DB server settings
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .retryWrites(true)
                    .build();

            // Connect to DB server
            MongoClient mongoClient = MongoClients.create(settings);

            // Create the DB
            MongoDatabase database = mongoClient.getDatabase(Database);

            // Create the needed collections
            CrawlerCollection = database.getCollection("Crawler");
            IndexerCollection = database.getCollection("Indexer");
            SeedCollection = database.getCollection("Seed");

            if (CrawlerCollection.countDocuments() >= MAX_PAGES_COUNT) {
                System.out.println("Recrawling from beginning");
                CrawlerCollection.drop();
                SeedCollection.drop();
            }

            checkSeed();
            System.out.println("Connected to DB");

        } catch (Exception e) {
            System.out.println("Not connected to DB");
            System.out.println(e);
        }
    }

    public int getSeedCount() {
        return (int) SeedCollection.countDocuments();
    }

    public void checkSeed() {
        if ((int) SeedCollection.countDocuments() == 0 && CrawlerCollection.countDocuments() < MAX_PAGES_COUNT) {
            try {
                File file = new File("seed.txt");
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    org.bson.Document url = new org.bson.Document("url", myReader.nextLine());
                    SeedCollection.insertOne(url);
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("No Seed file available, Use primitive seeds ");
                org.bson.Document seed = new org.bson.Document("url", "http://www.mathematinu.com/");
                SeedCollection.insertOne(seed);
                seed = new org.bson.Document("url", "http://eng.cu.edu.eg/ar/");
                SeedCollection.insertOne(seed);
                seed = new org.bson.Document("url", "https://stackoverflow.com/");
                SeedCollection.insertOne(seed);
            }
        } else {
            System.out.println("This crawler was interrupted before, resuming from where it stopped");
        }

    }

    public void insertSeed(String url) {
        org.bson.Document seed = new org.bson.Document("url", url);
        SeedCollection.insertOne(seed);
    }

    public Document popSeed() {
        return SeedCollection.findOneAndDelete(new org.bson.Document());
    }

    public FindIterable<Document> getSeed(String url) {
        return SeedCollection.find(new org.bson.Document("url", url));
    }

    public void insertpage(int id, String url, String doc) {
        org.bson.Document website = new org.bson.Document("id", id).append("url", url).append("content", doc);
        CrawlerCollection.insertOne(website);
    }

    public FindIterable<Document> getpage(int id) {
        return CrawlerCollection.find(new org.bson.Document("id", id));
    }

    public FindIterable<Document> getpage(String url) {
        return CrawlerCollection.find(new org.bson.Document("url", url));
        //This will return the whole document with the url & content
    }

    public int getPagesCount() {
        return (int) CrawlerCollection.countDocuments();
    }

    public FindIterable<Document> getAllPages() {
        return CrawlerCollection.find(new org.bson.Document());
        //This will return the whole document with the url & content
    }
}