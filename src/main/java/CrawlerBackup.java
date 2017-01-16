//import lombok.Getter;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//
///**
// * Created by Thomas VENNER on 13/01/2017.
// */
//public class CrawlerBackup {
//    private static Logger logger;
//
//    @Getter
//    private static ArrayList<String> urlCrawled;
//    @Getter
//    private static ArrayList<String> documentsCrawled;
//    private static ArrayList<String> documentsUncrawled;
//    private static int MAX_DOCUMENTS = 500;
//    private static int INDEX = 0;
//
//    public static void initialize() {
//        urlCrawled = new ArrayList<>();
//        documentsCrawled = new ArrayList<>();
//        documentsUncrawled = new ArrayList<>();
//        logger = LogManager.getLogger(CrawlerBackup.class);
//    }
//
//    public void crawl(String url_) {
//        if (documentsCrawled.size() >= MAX_DOCUMENTS) {
//            return;
//        }
//        if (isURLValid(url_)
//                && !hasBeenCrawled(url_)) {
//            urlCrawled.add(url_);
//            logger.info("Launching crawler {}", INDEX);
//            INDEX++;
//            Utils.startChrono();
//            Document document;
//            try {
////                logger.info("Trying to crawl {} ({} crawled)", url_, documentsCrawled.size());
//                document = Jsoup.parse(new URL(url_), 10000);
//            } catch (IOException e) {
////                logger.error("Unable to crawl");
////                logger.trace("\tcrawl: {}ms", Utils.endChrono());
//                documentsUncrawled.add(url_);
//                return;
//            }
//            documentsCrawled.add(url_);
////            logger.trace("\tcrawl: {}ms", Utils.endChrono());
//            new Indexer().treat(document);
//            for (final String url : getLinks(document)) {
//                if (url.startsWith("https://en.wikipedia.org/wiki/")
//                        && !url_.contains("#")
//                        && !url_.contains("/File:")
//                        && !url_.contains("/Category:")
//                        && !url_.contains("/Wikipedia:")
//                        && !url_.contains("/Portal:")
//                        && !url_.contains("/Template:")
//                        && !url_.contains("/Help:")
//                        && !url_.contains("/Talk:")
//                        && !url_.contains("/MOS:"))
//                    new CrawlerBackup().crawl(url);
//            }
//        }
//    }
//
//    private static boolean hasBeenCrawled(String url_) {
//        return urlCrawled.contains(url_);
//    }
//
//    private static ArrayList<String> getLinks(Document document_) {
//        ArrayList<String> links = new ArrayList<>();
//        for (Element element : document_.select("a")) {
//            doNothing();
//            String url = element.attr("abs:href");
//            if (url.equals("")) continue;
//            links.add(url);
//        }
//        return links;
//    }
//
//    private static boolean isURLValid(String url) {
//        try {
//            new URL(url);
//            return true;
//        } catch (MalformedURLException e) {
//            return false;
//        }
//    }
//
//    private static void doNothing() { }
//
//    public static int getDocumentsCount() {
//        return documentsCrawled.size();
//    }
//
//    public static void remove(String s) {
//        documentsCrawled.remove(s);
//    }
//
//    public static void increaseCapacity(int i) {
//        MAX_DOCUMENTS += i;
//    }
//}
