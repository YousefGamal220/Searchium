package DB;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.DBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import java.lang.Math;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class MongoDB {

    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> CrawlerCollection;
    MongoCollection<Document> IndexedPages;
    MongoCollection<Document> wordsCollection;
    MongoCollection<Document> SeedCollection;
    public static int MAX_PAGES_COUNT;
    int total_indexed_pages = 0;


    public MongoDB(String Database , int max) {
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
            IndexedPages = database.getCollection("IndexedPages");
            wordsCollection = database.getCollection("words");
            SeedCollection = database.getCollection("Seed");

            MAX_PAGES_COUNT = max;

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

    public void checkSeed()
    {

        if (CrawlerCollection.countDocuments() >= MAX_PAGES_COUNT) //it means that the crawler was done before
        {

            /**
             * @brief The Drop method delete all the documents and indexes
             * it may be replaced by
             * CrawlerCollection.deleteMany(new org.bson.Document());
             * */
            System.out.println("Crawling was completed before, crawl from beginning");
            CrawlerCollection.drop();
            SeedCollection.drop();
        }
        if((int)SeedCollection.countDocuments() == 0 && CrawlerCollection.countDocuments()<MAX_PAGES_COUNT) {
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

    // Indexer Interface
    public void insertPageInd(String url, int count)
    {
        org.bson.Document website = new org.bson.Document("url", url).append("count", count).append("finished", false);
        IndexedPages.insertOne(website);
    }

    public FindIterable<Document> findPageInd(String url)
    {
        return IndexedPages.find(new org.bson.Document("url", url));
    }

    public void insertWordInd(String word, String url)
    {
        org.bson.Document wordDoc = new org.bson.Document("word", word).append("IDF", 0.0f);

        ArrayList<DBObject> array = new ArrayList<DBObject>();
        BasicDBObject document = new BasicDBObject();

        document.put("url", url);
        document.put("counts_in", 1);
        array.add(document);
        wordDoc.put("pages", array);
        wordsCollection.insertOne(wordDoc);
    }

    public FindIterable<Document> getWordInd(String word)
    {
        return wordsCollection.find(new org.bson.Document("word", word));
    }

    public List<String> getUrlsForWordInd(String word)
    {
        Iterator<Document> itr = wordsCollection.find(new org.bson.Document("word", word)).iterator();
        List<String>urls = new ArrayList<String>();
        if (itr.hasNext())
        {
            for (Document page : itr.next().getList("pages", Document.class))
            {
                urls.add(page.getString("url"));
            }

        }
        return urls;
    }

    public void increaseWordCount(String word, String url)
    {

        int old_val = -1;

        BasicDBObject query = new BasicDBObject();
        query.put("word", word);

        Iterator<Document> wordDocItr = wordsCollection.find(query).iterator();
        if (wordDocItr.hasNext())
        {
            ArrayList<Document> array = new ArrayList<Document>();

            for (Document page : wordDocItr.next().getList("pages", Document.class))
            {
                if (!page.getString("url").equals(url))
                {
                    array.add(page);
                }
                else
                {
                    Document d = new Document();
                    d.put("url", url);
                    d.put("counts_in", page.getInteger("counts_in") + 1);
                    array.add(d);
                }
            }

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("pages", array); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            wordsCollection.updateOne(query, updateObject); // (4)
        }
    }

    public void insertNewUrl(String word, String url)
    {
        BasicDBObject query = new BasicDBObject();
        query.put("word", word);

        Iterator<Document> wordDocItr = wordsCollection.find(query).iterator();
        if (wordDocItr.hasNext())
        {
            ArrayList<Document> array = new ArrayList<Document>();

            for (Document page : wordDocItr.next().getList("pages", Document.class))
            {
                array.add(page);
            }

            Document d = new Document();
            d.put("url", url);
            d.put("counts_in", 1);
            d.put("tf", 0.0f);
            array.add(d);

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("pages", array); // (2)

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); // (3)

            wordsCollection.updateOne(query, updateObject); // (4)
        }

    }

    public boolean isIndexed(String url)
    {
        return IndexedPages.find(new org.bson.Document("url", url)).iterator().next().getBoolean("finished");
    }

    public void finishPageIndex(String url)
    {
        BasicDBObject query = new BasicDBObject();
        query.put("url", url); // (1)

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("finished", true); // (2)

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument); // (3)

        IndexedPages.updateOne(query, updateObject); // (4

        total_indexed_pages++;
    }


    public void calcTF(String word, String url, int t_n)
    {
        Document d = wordsCollection.find(new org.bson.Document("word", word).append("pages.url",url))
                .iterator().next();

        int t = 0;
        for (Document page : d.getList("pages", Document.class))
        {
            if (page.getString("url").equals(url))
                t = page.getInteger("counts_in");
        }

        wordsCollection.updateOne(new org.bson.Document("word", word).append("pages.url", url),
                new org.bson.Document("$set", new org.bson.Document("pages.$.TF", (float) t / t_n)));

    }

    public void calacIDF(int t_all)
    {
        Iterator<Document> wordItr = wordsCollection.find(new org.bson.Document()).iterator();
        while (wordItr.hasNext())
        {
            Document wordDoc = wordItr.next();
            int t = wordDoc.getList("pages", Document.class).size();
            wordsCollection.updateOne(new org.bson.Document("word", wordDoc.getString("word")),
                    new org.bson.Document("$set", new org.bson.Document("IDF", Math.log((float)total_indexed_pages / t))));
        }
    }

}