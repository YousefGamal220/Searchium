package DB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoDB {
    MongoClient mongoClient ;
    MongoDatabase database;
    MongoCollection Websites;
    MongoCollection Indexer;

    public MongoDB(){

    }
    public MongoDB(String Database , String URI) {


        try {

            ConnectionString connString = new ConnectionString(URI);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .retryWrites(true)
                    .build();
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(Database);
            Websites = database.getCollection("Websites");
            Indexer = database.getCollection("Indexer");

            System.out.println("Connected to DB");

        }catch (Exception e){
            System.out.println("Not connected to DB");

            System.out.println(e);

        }
    }

    Consumer<Document> printConsumer = new Consumer<Document>() {
        @Override
        public void accept(final Document document) {
            System.out.println(document.toJson());
        }
    };

    public void insertpage (int id, String url , String doc)
    {
        org.bson.Document website = new org.bson.Document("id",id).append("url",url).append("content",doc);
        Websites.insertOne(website);
    }

    public FindIterable getpage (int id)
    {
        return Websites.find(new org.bson.Document("id",id));
        //This will return the whole document with the url & content
    }

    public FindIterable getpage (String url)
    {

        return Websites.find(new org.bson.Document("url",url));
        //This will return the whole document with the url & content
    }
}