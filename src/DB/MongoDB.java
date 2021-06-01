package DB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
public class MongoDB {
    MongoClient client ;
    MongoDatabase database;
    MongoCollection<Document> Websites;
    MongoCollection<Document> Indexer;

    public MongoDB(){

    }
    public MongoDB(String Database , String URI) {


        try {
            client = MongoClients.create(URI);
            database = client.getDatabase(Database);
            Websites = database.getCollection("Websites");
            Indexer = database.getCollection("Indexed");
            System.out.println("Connected to DB");

        }catch (Exception e){
            System.out.println(e);

        }
    }

    public void insertpage (int id, String url , Document doc)
    {
        Document website = new Document("id",id).append("url",url).append("content",doc);
        Websites.insertOne(website);
    }

    public Document getpage (int id)
    {
        return Websites.find(new Document("id",id)).first();
        //This will return the whole document with the url & content
    }

    public Document getpage (String url)
    {
        return Websites.find(new Document("url",url)).first();
        //This will return the whole document with the url & content
    }
}