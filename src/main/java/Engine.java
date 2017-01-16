import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by SegFault on 14/01/2017.
 */
public class Engine extends Indexable implements Runnable {
    private static Logger logger;

    private Queue<Data> datas;
    private Queue<String> urlsCrawled;

    public static void initialize() {
        logger = LogManager.getLogger(Engine.class);
    }

    public Engine(Queue<Data> datas_, Queue<String> urlsCrawled_) {
        datas = datas_;
        urlsCrawled = urlsCrawled_;
    }

    public void run() {
        String query;
        try {
            while ((query = readStdin()) != null) {
                if (query.startsWith("!")) {
                    if (query.equals("!datas")) {
                        String[] _words = query.split(" +");
                        ArrayList<String> words = new ArrayList<>();
                        for (int i = 1; i < _words.length; i++) {
                            words.add(_words[i]);
                        }
                        logger.info(datas/*
                                .stream()
                                .filter(data -> words.contains(data.getWord()))
                                .collect(Collectors.toList())*/);
                    } else if (query.equals("!seePages")) {
                        logger.info("Pages crawled: {}", urlsCrawled.size());
                    }
                } else {
                    query = cleanUp(query);
                    ArrayList<Data> queryDoc = getDatas("query", query);
                    ArrayList<Data> tfIdfs = getTfIdfs(queryDoc);
//                    logger.info("tfIdfs: {}", tfIdfs);
                    foo(tfIdfs);
//                    logger.info("results for {}: {}", queryHashMap, search(queryHashMap, 3));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void foo(ArrayList<Data> tfIdfs) {
        tfIdfs.sort(Comparator.comparing(Data::getWord));
        logger.info(tfIdfs);
    }

    private static String readStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    private HashMap<String, Double> search(HashMap<String, Integer> query, int count) {
        HashMap<String, Double> results = new HashMap<>();
        ArrayList<String> words = new ArrayList<>();
//        query.entrySet().forEach(entry -> words.add(entry.getKey()));
//        ArrayList<Data> dataArray = getTfIdfs(query.keySet());
//        dataArray.stream().filter(data -> query.keySet().contains(data.getWord())).forEach(data -> {
//            String url = data.getUrl();
//            if (!results.keySet().contains(url)) {
//                results.put(url, query.get(data.getWord()) * data.getFrequency());
//            } else {
//                results.put(url, (query.get(data.getWord()) * data.getFrequency()) * results.get(url));
//            }
//        });
//        return results.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
//                .limit(count)
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return results;
    }

//    public static ArrayList<SearchResult> search(String query, int count, int documents) {
//        query = query.toLowerCase().trim();
//        if (Indexer.getLemmas().keySet().contains(query)) query = Indexer.getLemmas().get(query);
//        ArrayList<SearchResult> result = new ArrayList<>();
//        if (Indexer.getStopWords().contains(query)) return result;
//
//        ArrayList<Data> tfIdfs = Indexer.getTfIdfs(documents, query);
//        return Indexer.getReleventResults(query, count, tfIdfs);
//    }
//
//    public static ArrayList<SearchResult> search(String[] query, int count, int documents) {
//        ArrayList<SearchResult> result = new ArrayList<>();
//        ArrayList<Data> tfIdfs = new ArrayList<>();
//        for (String word : query) {
//            tfIdfs.addAll(Indexer.getTfIdfs(documents, word));
//        }
//
//        return Indexer.getReleventResults((ArrayList<String>) Arrays.stream(query).collect(Collectors.toList()), count, tfIdfs);
//    }

    private ArrayList<Data> getTfIdfs(ArrayList<Data> queryArray) {
        ArrayList<String> words = new ArrayList<>();
        for (Data data : queryArray) {
            words.add(data.getWord());
        }
        ArrayList<Data> datasArray = new ArrayList<>();
        HashMap<String, Long> wordsFrequency = new HashMap<>();
        datas
                .stream()
                .filter(data -> words.contains(data.getWord()))
                .forEach(data -> {
                    if (!wordsFrequency.containsKey(data.getWord())) {
                        wordsFrequency.put(data.getWord(), datas.stream().filter(d -> d.getWord().equals(data.getWord())).count() + 1);
                    }
                    Long frequency = wordsFrequency.get(data.getWord());
                    datasArray.add(new Data(
                            data.getUrl(),
                            data.getWord(),
                            frequency == 0 ?
                                    0 :
                                    data.getFrequency() * Math.log10((urlsCrawled.size() + 1) / frequency)
                    ));
                });
        queryArray
                .forEach(data -> {
                    if (!wordsFrequency.containsKey(data.getWord())) {
                        wordsFrequency.put(data.getWord(), datas.stream().filter(d -> d.getWord().equals(data.getWord())).count() + 1);
                    }
                    Long frequency = wordsFrequency.get(data.getWord());
                    datasArray.add(new Data(
                            data.getUrl(),
                            data.getWord(),
                            frequency == 0 ?
                                    0 :
                                    data.getFrequency() * Math.log10((urlsCrawled.size() + 1) / frequency)
                    ));
                });
        logger.info("words: {}", wordsFrequency);
//        logger.info("datas: {}", datas.stream().filter(data -> words.contains(data.getWord())).collect(Collectors.toList()));
        return datasArray;
    }
}
