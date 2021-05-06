package WebIndexer;

import java.util.ArrayList;
import java.util.List;


public class WebIndexer_test {
    private static void tokenizeWord_test1()
    {
        String sentence = "Welcome to Searchium";
        List<String> result = new ArrayList<String>() ;
        result.add("Welcome");
        result.add("to");
        result.add("Searchium");
        assert (Tokenizer.tokenizeWord(sentence) == result);
        System.out.println("Passed Testcase 1, tokenize String");
    }
    public static void main(String[] args) {
        tokenizeWord_test1();
    }
}
