package WebIndexer;

import DB.MongoDB;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebIndexerMain {
    MongoDB DB;
    List<String> stop_words;
    HashMap<String, List<Document>> index;

    public WebIndexerMain(MongoDB DB, List<String> stop_words) {
        this.DB = DB;
        this.stop_words = stop_words;
        index = new HashMap<String, List<Document>>();
    }

    public void runIndexer(String page, String page_url) throws IOException {

        HashMap<String, Integer> words_count = new HashMap<String, Integer>();
        page = page.replaceAll("[^a-zA-Z]", " ");
        List<String> words = Tokenizer.tokenizeWord(page);
        StopWordsRemover.removeStopWord(words, this.stop_words);
        int count = words.size();

        for (String word : words) {
            if (word.length() != 0) {
                Stemmer s = new Stemmer(word);
                String stemmed_word = s.toString();

                if (words_count.containsKey(stemmed_word)) {
                    words_count.put(word, words_count.get(stemmed_word) + 1);
                } else {
                    words_count.put(word, 1);
                }
            }
        }

        for (String stemmed_word : words_count.keySet()) {
            Document d = new Document();
            d.append("url", page_url);
            d.append("tf", words_count.get(stemmed_word) / (float) count);
            if (index.containsKey(stemmed_word)) {
                index.get(stemmed_word).add(d);
            } else {
                List<Document> arr = new ArrayList<>();
                arr.add(d);
                index.put(stemmed_word, arr);
            }
        }
    }

    public void updateIndexerDB() {
        for (String word : index.keySet()) {
            DB.insertWord(word, index.get(word));
        }
    }
}
