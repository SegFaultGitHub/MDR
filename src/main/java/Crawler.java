import io.vertx.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Crawler {
    private static long MAX_DOCUMENTS = 50;
    private static Logger logger;

    private static Queue<String> urlsCrawled;

    public static void initialize() {
        logger = LogManager.getLogger(Crawler.class);
        urlsCrawled = new ConcurrentLinkedDeque<>();
    }

    private EventBus eventBus;

    public Crawler(EventBus eventBus_) {
        eventBus = eventBus_;
    }

    public void crawl(String url) {
        if (urlsCrawled.size() < MAX_DOCUMENTS) {
            if (url != null) {
                if (isURLValid(url)) {
                    if (!urlsCrawled.contains(url)) {
                        urlsCrawled.add(url);
                        logger.info("Crawling {}", url);
                        Document document;
                        try {
                            document = Jsoup.parse(new URL(url), 10000);
                        } catch (IOException e) {
                            logger.error("Unable to crawl {}", url);
                            return;
                        }
                        eventBus.send("/api/index", document);
                        for (String externUrl : getLinks(document)) {
                            eventBus.send("/api/crawl", externUrl);
                        }
                    }
                } else {
                    logger.info("Invalid URL: {}", url);
                }
            } else {
                logger.error("URL null");
            }
        }
    }

    private static ArrayList<String> getLinks(Document document_) {
        ArrayList<String> links = new ArrayList<>();
        for (Element element : document_.select("a")) {
            doNothing();
            String url = element.attr("abs:href");
            if (url.equals("")) continue;
            links.add(url);
        }
        return links;
    }

    private static boolean isURLValid(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static void doNothing() { }
}
