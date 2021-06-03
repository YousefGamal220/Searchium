package DB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoDB {
    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> CrawlerCollection;
    MongoCollection<Document> IndexerCollection;

    public MongoDB(String Database) {
        try {
            ConnectionString connString = new ConnectionString("mongodb://localhost:27017/?readPreference=primary&appname=MongoDB%20Compass&ssl=false");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .retryWrites(true)
                    .build();
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(Database);
            CrawlerCollection = database.getCollection("Crawler");
            IndexerCollection = database.getCollection("Indexer");

            System.out.println("Connected to DB");

        } catch (Exception e) {
            System.out.println("Not connected to DB");
            System.out.println(e);
        }
    }

    Consumer<Document> printConsumer = document -> System.out.println(document.toJson());

    public void insertpage(int id, String url, String doc) {
        org.bson.Document website = new org.bson.Document("id", id).append("url", url).append("content", doc);
        CrawlerCollection.insertOne(website);
    }

    public FindIterable<Document> getpage(int id) {
        return CrawlerCollection.find(new org.bson.Document("id", id));
        //This will return the whole document with the url & content
    }

    public FindIterable<Document> getpage(String url) {
        return CrawlerCollection.find(new org.bson.Document("url", url));
        //This will return the whole document with the url & content
    }

    public int getPagesCount() {
        return (int)CrawlerCollection.countDocuments();
    }
}