import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Main {
    private static Logger logger;

    private static ArrayList<Crawler> crawlers;

    public static void main(String[] args) throws IOException {
        crawlers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            crawlers.add(new Crawler());
        }

        logger = LogManager.getLogger(Main.class);
        Utils.startChrono();
        Indexer.initialize();
        logger.trace("Indexer init: {}ms", Utils.endChrono());
        Utils.startChrono();
        Crawler.initialize();
        logger.trace("Crawler init: {}ms", Utils.endChrono());
        Utils.startChrono();
        Engine.initialize();
        logger.trace("Engine init: {}ms", Utils.endChrono());
        Utils.startChrono();
        for (Crawler crawler : crawlers) {
            crawler.crawl("https://en.wikipedia.org/wiki/France");
        }
        logger.trace("crawler: {}ms", Utils.endChrono());
        String query;
        while ((query = readStdin()) != null) {
            Utils.startChrono();
            int count = 10;
            ArrayList<SearchResult> results;
            if (query.startsWith("!")) {
                /*if (query.equals("!crawl")) {
                    Utils.startChrono();
                    Crawler.increaseCapacity(50);
                    Crawler.remove("https://en.wikipedia.org/wiki/Special:Random");
                    Crawler.crawl("https://en.wikipedia.org/wiki/Special:Random");
                    logger.trace("crawler: {}ms", Utils.endChrono());
                } else if (query.startsWith("!crawl ")) {
                    Utils.startChrono();
                    Crawler.increaseCapacity(5);
                    Crawler.crawl(query.split(" ")[1]);
                    logger.trace("crawler: {}ms", Utils.endChrono());
                } else */if (query.equals("!seePages")) {
                    logger.info("Pages crawled: {}\n{}", Crawler.getDocumentsCount(), Crawler.getDocumentsCrawled());
                }
            } else {
                if (query.contains(" ")) {
                    results = Engine.search(query.split(" "), count, Crawler.getDocumentsCount());
                } else {
                    results = Engine.search(query, count, Crawler.getDocumentsCount());
                }
                if (results != null && results.size() != 0) {
                    boolean first = true;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("[\n");
                    for (SearchResult result : results) {
                        if (!first) {
                            stringBuilder.append(",\n");
                        }
                        first = false;
                        stringBuilder.append("\t" + result.toString());
                    }
                    stringBuilder.append("\n]");
                    logger.info("Search result for \"{}\":\n{}", query, stringBuilder);
                    logger.trace("search \"{}\": ", query, Utils.endChrono() + "ms");
                } else {
                    logger.info("No results for \"{}\"", query);
                    logger.trace("search \"{}\": {}", query, Utils.endChrono() + "ms");
                }
            }
        }
    }

    private static String readStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    private static void doNothing() { }
}
