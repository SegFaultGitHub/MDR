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

//    public static ArrayList<SearchResult> getReleventResults(String word, int count, ArrayList<Data> datas) {
//        ArrayList<Data> array = (ArrayList<Data>) datas.stream().filter(d -> d.getWord().equals(word)).sorted(
//                (d1, d2) -> (int) Math.signum(d2.getFrequency() - d1.getFrequency())
//        ).limit(count).collect(Collectors.toList());
//        ArrayList<SearchResult> results = new ArrayList<>();
//        array.forEach(data -> results.add(new SearchResult(data.getUrl(), data.getFrequency())));
//        return results;
//    }
//
//    public static ArrayList<SearchResult> getReleventResults(ArrayList<String> words, int count, ArrayList<Data> datas) {
//        datas.sort((d1, d2) -> (int) Math.signum(d2.getFrequency() - d1.getFrequency()));
//        HashMap<String, Double> hashMap = new HashMap<>();
//        ArrayList<SearchResult> results = new ArrayList<>();
//        datas.stream().filter(data -> words.contains(data.getWord())).forEach(data -> {
//            String url = data.getUrl();
//            if (!hashMap.keySet().contains(url)) {
//                hashMap.put(url, 1 + data.getFrequency());
//            } else {
//                hashMap.put(url, (1 + data.getFrequency()) * hashMap.get(url));
//            }
//        });
//        for (HashMap.Entry<String, Double> entry : hashMap.entrySet()) {
//            results.add(new SearchResult(entry));
//        }
//        return (ArrayList<SearchResult>) results.stream().sorted(
//                (r1, r2) -> (int) Math.signum(r2.getTfIdf() - r1.getTfIdf())
//        ).limit(count).collect(Collectors.toList());
//    }
}
