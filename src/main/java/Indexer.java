import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Indexer extends Indexable implements Runnable {
    private static Logger logger;

    private Queue<Document> documents;
    private Queue<Data> datas;

    public static void initialize() {
        logger = LogManager.getLogger(Indexer.class);
    }

    public Indexer(Queue<Data> datas_, Queue<Document> documents_) {
        datas = datas_;
        documents = documents_;
    }

    public void run() {
        while (true) {
            Document document;
            if ((document = documents.poll()) != null) {
                logger.info("Indexing {}", document.location());
                if (document.body() == null) return;
                datas.addAll(getDatas(document.location(), document.body().text()));
            }
        }
    }
}
