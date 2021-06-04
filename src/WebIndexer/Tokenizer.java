package WebIndexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * The type Tokenizer.
 */
public class Tokenizer {
    /**
     * Tokenize word list.
     *
     * @param sentence the sentence
     * @return the list
     */
    public static List<String> tokenizeWord(String sentence)
    {
        /**
         * This Function Returns the tokenization of a given sentence<String>
         * it takes String Parameter sentence
         * And Returns List of Strings the Tokens

         * Example: Given Sentence "Welcome to Searchium" as input
         * Output: ["Welcome", "to", "Searchium"]
         */

        return new ArrayList<String>(Arrays.asList(sentence.split(" ")));
    }

    /**
    * The Following two functions are overrided
    * They do the same functionality
    * but the difference in the input
    * The First Function takes parameter List<String> for example:
    * ["Welcome to Searchium", "This is a Search Engine Project", "By CMP-2023"]
    *
    * The Second Function takes parameter Full Corpus and regex splitter for example \n OR <br>
    * The First thing it do is to convert the corpus to the form of the first function as List of Strings,
    * then it do the same functionality as the first mentioned function
     */

    /**
     * Tokenize corpus list.
     *
     * @param corpus the corpus
     * @return the list
     */
    public static List<List<String>> tokenizeCorpus(List<String> corpus)
    {

        List<List<String>> tokenizedCorpus = new ArrayList<List<String>>();

        Consumer<String> tokenization = sentence -> tokenizedCorpus.add(tokenizeWord(sentence));

        corpus.forEach(tokenization);

        return tokenizedCorpus;
    }


    /**
     * Tokenize corpus list.
     *
     * @param corpus   the corpus
     * @param splitter the splitter
     * @return the list tokenizedCorpus
     */
    public static List<List<String>> tokenizeCorpus(String corpus, String splitter)
    {

        List<String> sentences = new ArrayList<String>(Arrays.asList(corpus.split(splitter)));
        List<List<String>> tokenizedCorpus = new ArrayList<List<String>>();
        Consumer<String> tokenization = sentence -> tokenizedCorpus.add(tokenizeWord(sentence));
        sentences.forEach(tokenization);
        return tokenizedCorpus;
    }

}
