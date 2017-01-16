import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by SegFault on 16/01/2017.
 */
public class Manager {
    private static Logger logger;

    private final int THREADS = 10;

    private Queue<String> urlsCrawled;
    private Queue<String> urlsToCrawl;
    private Queue<Document> documents;
    private Queue<Data> datas;

    public Manager() {
        logger = LogManager.getLogger(Manager.class);

        Crawler.initialize();
        Indexer.initialize();
        Indexable.initialize();
        Engine.initialize();

        urlsCrawled = new ConcurrentLinkedDeque<>();
        urlsToCrawl = new ConcurrentLinkedDeque<>();
        documents = new ConcurrentLinkedDeque<>();
        datas = new ConcurrentLinkedDeque<>();

        urlsToCrawl.add("https://en.wikipedia.org/wiki/France");
    }

    public void exectute() throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < THREADS; i++) {
            executor.execute(() -> new Thread(new Crawler(urlsCrawled, urlsToCrawl, documents)).start());
            executor.execute(() -> new Thread(new Indexer(datas, documents)).start());
        }

        executor.execute(() -> new Thread(new Engine(datas, urlsCrawled)).start());
    }
}
