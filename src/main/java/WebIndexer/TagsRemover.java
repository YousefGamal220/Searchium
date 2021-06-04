package WebIndexer;

public class TagsRemover {

    /**
     * Remove HTML tags from a page.
     *
     * @param page in String format
     * @return the content of page only.
     */
    public static String removeTags(String page) {
        /**
         * This Function Returns the content of a given page<String>
         * it takes String Parameter page
         * And Returns a String after removing all tags
         * Example: Given page "<p><Welcome to Searchium </>" as input
         * Output: "Welcome to Searchium"
         */

        return page.replaceAll("<[^>]*>", "");
    }
}
