import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by SegFault on 14/01/2017.
 */
public class Engine {
    public static Logger logger;

    public static void initialize() {
        logger = LogManager.getLogger(Engine.class);
    }

    public static ArrayList<SearchResult> search(String query, int count, int documents) {
        query = query.toLowerCase().trim();
        if (Indexer.getLemmas().keySet().contains(query)) query = Indexer.getLemmas().get(query);
        ArrayList<SearchResult> result = new ArrayList<>();
        if (Indexer.getStopWords().contains(query)) return result;

        ArrayList<Data> tfIdfs = Indexer.getTfIdfs(documents, query);
        return Indexer.getReleventResults(query, count, tfIdfs);
    }

    public static ArrayList<SearchResult> search(String[] query, int count, int documents) {
        ArrayList<SearchResult> result = new ArrayList<>();
        ArrayList<Data> tfIdfs = new ArrayList<>();
        for (String word : query) {
            tfIdfs.addAll(Indexer.getTfIdfs(documents, word));
        }

        return Indexer.getReleventResults((ArrayList<String>) Arrays.stream(query).collect(Collectors.toList()), count, tfIdfs);
    }
}
