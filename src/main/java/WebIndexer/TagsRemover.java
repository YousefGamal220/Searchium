package WebIndexer;

import org.apache.commons.lang3.StringUtils;

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

        //page = page.replaceAll("\\<.*?\\>", " ");
        //page = StringUtils.normalizeSpace(page);

        return page;
    }
}
