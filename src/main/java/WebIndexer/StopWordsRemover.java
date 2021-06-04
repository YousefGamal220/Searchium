package WebIndexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StopWordsRemover {

    /**
     * Remove stopping words from a page.
     *
     * @param corpus_path to load words from.
     * @return List of all stopping words.
     */
    public static List<String> buildStopWordsCorpus(String corpus_path) throws IOException {
        /**
         * This Function remove creates a list of stopping words
         * from a given file path.
         */
        return Files.readAllLines(Paths.get(corpus_path));
    }

    /**
     * Remove stopping words from a page.
     *
     * @param page in String format.
     * @param corpus stopping words to be removed.
     * @return void.
     */
    public static void removeStopWord(List<String> page, List<String> corpus) {
        /**
         * This Function remove stopping words from a page ArrayList<String>
         * it takes ArrayList<String> Parameter page
         * And transform the list
         * Example: Given page "["Welcome", "to", "Searchium"]" as input
         * Output: "["Welcome", "Searchium"].
         */
         page.removeAll(corpus);
    }

}
