import javafx.util.Pair;
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
                        ArrayList<Data> dataArray = new ArrayList<>();
                        for (int i = 1; i < _words.length; i++) {
                            final int finalI = i;
                            dataArray.addAll(datas
                                    .stream()
                                    .filter(data -> data.getWord().equals(_words[finalI]))
                                    .collect(Collectors.toList()));
                        }
                        logger.info(datas);
                    } else if (query.equals("!seePages")) {
                        logger.info("Pages crawled: {}", urlsCrawled.size());
                    }
                } else {
                    int count = 3;
                    ArrayList<Data> queryDoc = getDatas("query", cleanUp(query));
                    ArrayList<Data> tfIdfs = getTfIdfs(queryDoc);
//                    logger.info("tfIdfs: {}", tfIdfs);
                    List<Pair<String, Double>> vectors = orderedSearch(tfIdfs, count);
                    if (vectors == null || vectors.isEmpty()) {
                        logger.info("No result for \"{}\"", query);
                    } else {
                        String results = "";
                        for (Pair p : vectors) {
                            results += "\n\t" + p.getKey() + " (" + p.getValue() + ")";
                        }
                        logger.info("Most relevant results for \"{}\": {}", query, results);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Pair<String, Double>> getVectors(ArrayList<Data> tfIdfs) {
        tfIdfs.sort(Comparator.comparing(Data::getWord));
        HashMap<String, ArrayList<Double>> h = new HashMap<>();
        for (Data data : tfIdfs) {
            if (!data.getUrl().equals("query") && !h.keySet().contains(data.getUrl())) {
                h.put(data.getUrl(), new ArrayList<>());
            }
        }
        ArrayList<Double> queryVector = new ArrayList<>();
        for (Data d : tfIdfs.stream().filter(data -> data.getUrl().equals("query")).collect(Collectors.toList())) {
            queryVector.add(d.getFrequency());
            for (String key : h.keySet()) {
                List<Data> entry = tfIdfs
                        .stream()
                        .filter(data -> d.getWord().equals(data.getWord()) && key.equals(data.getUrl()))
                        .limit(1)
                        .collect(Collectors.toList());
                if (entry.isEmpty()) {
                    h.get(key).add(0d);
                }
                else {
                    h.get(key).add(entry.get(0).getFrequency());
                }
            }
        }
        DocumentVector queryDocumentVector = new DocumentVector("query", queryVector);
        ArrayList<DocumentVector> vectors = new ArrayList<>();
        for (String key : h.keySet()) {
            vectors.add(new DocumentVector(key, h.get(key)));
        }
        ArrayList<Pair<String, Double>> dotProducts = new ArrayList<>();
        for (DocumentVector doc : vectors) {
            dotProducts.add(new Pair<>(doc.getUrl(), queryDocumentVector.dotProduct(doc)));
        }
        return dotProducts;
    }

    private List<Pair<String, Double>> search(ArrayList<Data> tfIdfs, int count) {
        ArrayList<Pair<String, Double>> dotProducts = getVectors(tfIdfs);

        return dotProducts
                .stream()
                .sorted((p1, p2) -> (int) Math.signum(p2.getValue() - p1.getValue()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private List<Pair<String, Double>> orderedSearch(ArrayList<Data> tfIdfs, int count) {
        ArrayList<Pair<String, Double>> dotProducts = getVectors(tfIdfs);
        List<Data> query = tfIdfs
                .stream()
                .filter(data -> data.getUrl().equals("query"))
                .collect(Collectors.toList());
        List<String> urls = new ArrayList<>();
        for (Data data : tfIdfs
                .stream()
                .filter(data -> !data.getUrl().equals("query"))
                .collect(Collectors.toList())) {
            if (!urls.contains(data.getUrl())) {
                urls.add(data.getUrl());
            }
        }
        logger.info("urls: {}", urls);
        for (String url : urls) {
            List<Data> datasUrl = tfIdfs
                    .stream()
                    .filter(data -> data.getUrl().equals(url))
                    .collect(Collectors.toList());
            if (isOrdered(query, datasUrl)) {
                for (int i = 0; i < dotProducts.size(); i++) {
                    if (dotProducts.get(i).getKey().equals(url)) {
                        Pair<String, Double> pair = dotProducts.get(i);
                        dotProducts.remove(i);
                        dotProducts.add(new Pair<>(pair.getKey(), pair.getValue() * 2));
                        break;
                    }
                }
            }
        }
        return dotProducts
                .stream()
                .sorted((p1, p2) -> (int) Math.signum(p2.getValue() - p1.getValue()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isOrdered(List<Data> query, List<Data> document) {
        List<String> wordsQuery = makeSentence(query);
        List<String> wordsDocument = makeSentence(document);
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < wordsDocument.size(); i++) {
            if (wordsDocument.get(i).equals(wordsQuery.get(0))) {
                positions.add(i);
            }
        }
        boolean result = false;
        for (int position : positions) {
            int n = 0;
            boolean good = true;
            for (String word : wordsQuery) {
                if (position + n >= wordsDocument.size() || !wordsDocument.get(position + n).equals(word)) {
                    good = false;
                    break;
                }
                n++;
            }
            if (good) {
                result = true;
                break;
            }
        }
        return result;
    }

    private ArrayList<String> makeSentence(List<Data> datas) {
        ArrayList<String> words = new ArrayList<>();
        for (Data data : datas) {
            for (int pos : data.getPositions()) {
                for (int i = words.size(); i <= pos; i++) {
                    words.add("");
                }
                words.remove(pos);
                words.add(pos, data.getWord());
            }
        }
        return words;
    }

    private static String readStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

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
                        wordsFrequency.put(data.getWord(), datas
                                .stream()
                                .filter(d -> d.getWord().equals(data.getWord()))
                                .count() + 1);
                    }
                    Long frequency = wordsFrequency.get(data.getWord());
                    datasArray.add(new Data(
                            data.getUrl(),
                            data.getWord(),
                            frequency == 0 ?
                                    0 :
                                    data.getFrequency() * Math.log10((urlsCrawled.size() + 1) / frequency),
                            data.getPositions()));
                });
        queryArray
                .forEach(data -> {
                    if (!wordsFrequency.containsKey(data.getWord())) {
                        wordsFrequency.put(data.getWord(), datas
                                .stream()
                                .filter(d -> d.getWord().equals(data.getWord()))
                                .count() + 1);
                    }
                    Long frequency = wordsFrequency.get(data.getWord());
                    datasArray.add(new Data(
                            data.getUrl(),
                            data.getWord(),
                            frequency == 0 ?
                                    0 :
                                    data.getFrequency() * Math.log10((urlsCrawled.size() + 1) / frequency),
                            data.getPositions()));
                });
        return datasArray;
    }
}
