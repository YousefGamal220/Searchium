package WebIndexer;

import java.io.IOException;
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
    public  void runIndexer(String page, List<String> stop_words) throws IOException {

        String page_content =  TagsRemover.removeTags(page);

        List<String> words = Tokenizer.tokenizeWord(page_content);
        StopWordsRemover.removeStopWord(words, stop_words);

       for (String word : words)
        {
            word = word.replaceAll("[^a-zA-Z0-9]", "");
            Stemmer s = new Stemmer(word);
            String stemed_word = s.toString();

            System.out.print(word);
            System.out.print("      ");
            System.out.println(stemed_word);


        }

    }
}
