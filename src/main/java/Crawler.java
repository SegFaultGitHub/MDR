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

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Crawler implements Runnable {
    private final static int MAX_DOCUMENTS = 10;
    private static Logger logger;

    private Queue<String> urlsCrawled;
    private Queue<String> urlsToCrawl;
    private Queue<Document> documents;

    public static void initialize() {
        logger = LogManager.getLogger(Crawler.class);
    }

    public Crawler(Queue<String> urlsCrawled_, Queue<String> urlsToCrawled_, Queue<Document> documents_) {
        urlsCrawled = urlsCrawled_;
        urlsToCrawl = urlsToCrawled_;
        documents = documents_;
    }

    public void run() {
        while (urlsCrawled.size() < MAX_DOCUMENTS) {
            String url;
            if ((url = urlsToCrawl.poll()) != null) {
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
                        documents.add(document);
                        for (String externUrl : getLinks(document)) {
                            if (externUrl.startsWith("https://en.wikipedia.org/wiki/")
                                    && !externUrl.contains("#")
                                    && !externUrl.contains("/File:")
                                    && !externUrl.contains("/Category:")
                                    && !externUrl.contains("/Wikipedia:")
                                    && !externUrl.contains("/Portal:")
                                    && !externUrl.contains("/Template:")
                                    && !externUrl.contains("/Help:")
                                    && !externUrl.contains("/Talk:")
                                    && !externUrl.contains("/MOS:"))
                                urlsToCrawl.add(externUrl);
                        }
                    }
                }
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
