package WebIndexer;

import DB.MongoDB;
import org.bson.Document;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


/**
 * WebIndexer Stages
 * 1- Tokenizer (Gamal) Done
 * 2- Remove Stopping Words [Optional: To make it Smarter] (Yahia) Done
 * 3- Remove HTML tags (Yahia) Done
 * 4- Stemming [Smart Stemmer] (Gamal) Done
 * 5- Indexing and TF-IDF (Yahia and Gamal)
 * 6- Integrating Module and Testing of simple Corpus (Yahia and Gamal)
 */

public class WebIndexerMain {

    MongoDB DB;

    public WebIndexerMain(MongoDB DB) {
        this.DB = DB;

    }

    public void runIndexer(String page, String page_url, List<String> stop_words) throws IOException {
        Iterator<Document> pageItr = DB.findPageInd(page_url).iterator();
        if (pageItr.hasNext() && DB.isIndexed(page_url))
            return;

        String page_content = TagsRemover.removeTags(page);
        page_content = page_content.replaceAll("[^a-zA-Z]", " ");
        List<String> words = Tokenizer.tokenizeWord(page_content);
        StopWordsRemover.removeStopWord(words, stop_words);

        if (!pageItr.hasNext()) {
            DB.insertPageInd(page_url, words.size());
            System.out.println("new page added");
        }


        for (String word : words) {
            if (word.length() != 0) {
                Stemmer s = new Stemmer(word);
                String stemed_word = s.toString();

                //System.out.println(word);
                Iterator<Document> wordItr = DB.getWordInd(stemed_word).iterator();
                if (!wordItr.hasNext()) {
                    DB.insertWordInd(stemed_word, page_url);
                } else {
                    boolean found = false;
                    for (String url : DB.getUrlsForWordInd(stemed_word)) {
                        if (page_url.equals(url)) {
                            found = true;
                            DB.increaseWordCount(stemed_word, page_url);
                            break;
                        }
                    }
                    if (!found)
                        DB.insertNewUrl(stemed_word, page_url);
                }
            }
        }

        DB.finishPageIndex(page_url);
    }
}
