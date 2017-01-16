import java.io.IOException;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Manager manager = new Manager();
        manager.exectute();
//        String query;
//        while ((query = readStdin()) != null) {
//            Utils.startChrono();
//            int count = 10;
//            ArrayList<SearchResult> results;
//            if (query.startsWith("!")) {
//                if (query.equals("!crawl")) {
//                    Utils.startChrono();
//                    Crawler.increaseCapacity(50);
//                    Crawler.remove("https://en.wikipedia.org/wiki/Special:Random");
//                    Crawler.crawl("https://en.wikipedia.org/wiki/Special:Random");
//                    logger.trace("crawler: {}ms", Utils.endChrono());
//                } else if (query.startsWith("!crawl ")) {
//                    Utils.startChrono();
//                    Crawler.increaseCapacity(5);
//                    Crawler.crawl(query.split(" ")[1]);
//                    logger.trace("crawler: {}ms", Utils.endChrono());
//                } else if (query.equals("!seePages")) {
//                    logger.info("Pages crawled: {}\n{}", Crawler.getDocumentsCount(), Crawler.getDocumentsCrawled());
//                }
//            } else {
//                if (query.contains(" ")) {
//                    results = Engine.search(query.split(" "), count, Crawler.getDocumentsCount());
//                } else {
//                    results = Engine.search(query, count, Crawler.getDocumentsCount());
//                }
//                if (results != null && results.size() != 0) {
//                    boolean first = true;
//                    StringBuilder stringBuilder = new StringBuilder();
//                    stringBuilder.append("[\n");
//                    for (SearchResult result : results) {
//                        if (!first) {
//                            stringBuilder.append(",\n");
//                        }
//                        first = false;
//                        stringBuilder.append("\t" + result.toString());
//                    }
//                    stringBuilder.append("\n]");
//                    logger.info("Search result for \"{}\":\n{}", query, stringBuilder);
//                    logger.trace("search \"{}\": ", query, Utils.endChrono() + "ms");
//                } else {
//                    logger.info("No results for \"{}\"", query);
//                    logger.trace("search \"{}\": {}", query, Utils.endChrono() + "ms");
//                }
//            }
//        }
    }
}
