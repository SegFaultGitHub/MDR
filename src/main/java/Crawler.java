import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Crawler {
    private static Logger logger;

    @Getter
    private static ArrayList<String> urlToCrawl;
    private static ArrayList<String> documentsCrawled;
    private static ArrayList<String> documentsUncrawled;
    private static int MAX_DOCUMENTS = 500;

    public static void initialize() {
        urlToCrawl = new ArrayList<>();
        documentsCrawled = new ArrayList<>();
        documentsUncrawled = new ArrayList<>();
        logger = LogManager.getLogger(Crawler.class);
    }

    public Crawler() { }

    public void crawl(String url_) {
        if (documentsCrawled.size() >= MAX_DOCUMENTS) {
            return;
        }
        if (isURLValid(url_)
                && !hasBeenCrawled(url_)) {
            Utils.startChrono();
            Document document;
            try {
                logger.info("Trying to crawl {} ({} crawled)", url_, documentsCrawled.size());
                document = Jsoup.parse(new URL(url_), 10000);
            } catch (IOException e) {
                logger.error("Unable to crawl");
                logger.trace("\tcrawl: {}ms", Utils.endChrono());
                documentsUncrawled.add(url_);
                return;
            }
            documentsCrawled.add(url_);
            logger.trace("\tcrawl: {}ms", Utils.endChrono());
            Indexer.treat(document);
            for (final String url : getLinks(document)) {
                if (url.startsWith("https://en.wikipedia.org/wiki/")
                        && !url.contains("#")
                        && !url.contains("/File:")
                        && !url.contains("/Category:")
                        && !url.contains("/Wikipedia:")
                        && !url.contains("/Portal:")
                        && !url.contains("/Template:")
                        && !url.contains("/Help:")
                        && !url.contains("/Talk:")
                        && !url.contains("/MOS:"))
                    crawl(url);
            }
        }
    }

    private static boolean hasBeenCrawled(String url_) {
        for (int i = 0; i < documentsCrawled.size(); i++) {
            if (documentsCrawled.get(i).equals(url_)) return true;
        }
        for (int i = 0; i < documentsUncrawled.size(); i++) {
            if (documentsUncrawled.get(i).equals(url_)) return true;
        }
        return false;
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

    public static int getDocumentsCount() {
        return documentsCrawled.size();
    }

    public static void remove(String s) {
        documentsCrawled.remove(s);
    }

    public static void increaseCapacity(int i) {
        MAX_DOCUMENTS += i;
    }
}
