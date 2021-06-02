import DB.MongoDB;
import WebCrawler.WebCrawler;

public class App {
    public static void main(String[] args) throws Throwable {
        System.out.println("Searchium Main is Called");
        MongoDB db = new MongoDB("Searchium","mongodb://localhost:27017/?readPreference=primary&appname=MongoDB%20Compass&ssl=false");
        String[] URLs = {"http://www.mathematinu.com/", "http://eng.cu.edu.eg/ar/"};
        Thread[] crawlers = new Thread[URLs.length];
        for (int i = 0; i < URLs.length; i++) {
            crawlers[i] = new Thread(new WebCrawler(db, URLs[i]));
            crawlers[i].setName("Crawler " + (i + 1));
            crawlers[i].start();
        }
        for (Thread crawler : crawlers) {
            crawler.join();
        }


        //WebIndexerMain webIndexerMain = new WebIndexerMain(); // Creating Instance from the class
    }
}