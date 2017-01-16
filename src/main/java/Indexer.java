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
import java.util.stream.Collectors;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Indexer {
    @Getter
    private static ArrayList<String> stopWords;
    @Getter
    private static HashMap<String, String> lemmas;
    private static HashMap<String, Integer> lemmasCount;
    private static ArrayList<Data> dataArray;
    private static Logger logger;
    private static Whitelist whitelist;

    public static void initialize() {
        logger = LogManager.getLogger(Indexer.class);
        stopWords = new ArrayList<>();
        String stopWordsFile = "src/main/resources/stop-words-en.txt";
        try {
            FileReader fileReader = new FileReader(stopWordsFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    Collections.addAll(stopWords, line.split(","));
                }
                logger.info("Stop words loaded");
            } catch (IOException e) {
                logger.error("Cannot read line from " + stopWordsFile);
            } finally {
                fileReader.close();
                bufferedReader.close();
            }
        } catch (FileNotFoundException e) {
            logger.error("Cannot open " + stopWordsFile);
        } catch (IOException e) {
            logger.error("IOException");
        }
        lemmas = new HashMap<>();
        String lemmasFile = "src/main/resources/lemmatization-en.txt";
        try {
            FileReader fileReader = new FileReader(lemmasFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    String[] lemma = line.split(",");
                    lemmas.put(lemma[1].toLowerCase(), lemma[0].toLowerCase());
                }
                logger.info("Lemmas loaded");
            } catch (IOException e) {
                logger.error("Cannot read line from " + stopWordsFile);
            } finally {
                fileReader.close();
                bufferedReader.close();
            }
        } catch (FileNotFoundException e) {
            logger.error("Cannot open " + lemmasFile);
        } catch (IOException e) {
            logger.error("IOException");
        }

        lemmasCount = new HashMap<>();
        dataArray = new ArrayList<>();
        whitelist = Whitelist.none();
    }

    public static void reset() {
        lemmasCount.clear();
    }

    public static void treat(Document document) {
        if (document != null) {
            Utils.startChrono();
            Utils.startChrono();
            String clean = cleanUp(document);
            logger.trace("\t\tcleanUp: {}ms", Utils.endChrono());
            Utils.startChrono();
            reduceAndTokenize(clean);
            logger.trace("\t\treduce: {}ms", Utils.endChrono());
            Utils.startChrono();
            fillDataArray(document.location());
            logger.trace("\t\tfill data: {}ms", Utils.endChrono());
            logger.trace("\ttreatment: {}ms", Utils.endChrono());
            reset();
        }
    }

    private static String cleanUp(Document document) {
//        document.select(":containsOwn(\u00a0)").remove();
        if (document.body() == null) return "";
        String clean = document.body().text();
        clean = clean.toLowerCase().replaceAll("[^a-z0-9\\-']+", " ");
        return clean;
    }

    public static void reduceAndTokenize(String input) {
        ArrayList<String> cleanSplit = new ArrayList<>();
        Collections.addAll(cleanSplit, input.split(" +"));
        for (int i = 0; i < cleanSplit.size(); i++) {
            String lemma;
            String word = cleanSplit.get(i);
            if (lemmas.containsKey(word)) {
                lemma = lemmas.get(word);
            } else {
                lemma = word;
            }
            if (lemmasCount.containsKey(lemma)) {
                lemmasCount.put(lemma, lemmasCount.get(lemma) + 1);
            } else if (!stopWords.contains(lemma)) {
                lemmasCount.put(lemma, 1);
            }
        }
    }

    private static void fillDataArray(String url) {
        double D = 0;
        for (String key : lemmasCount.keySet()) {
            D += lemmasCount.get(key) * lemmasCount.get(key);
        }
        D = Math.sqrt(D);
        for (String key : lemmasCount.keySet()) {
            dataArray.add(new Data(url, key, /*index++,*/ (double) lemmasCount.get(key) / D));
        }
    }

    public static ArrayList<Data> getTfIdfs(int documents, String word) {
        ArrayList<Data> datas = new ArrayList<>();
        HashMap<String, Long> wordsFrequency = new HashMap<>();
        dataArray.stream().filter(data -> data.getWord().equals(word)).forEach(data -> {
            if (!wordsFrequency.containsKey(word)) {
                wordsFrequency.put(word, dataArray.stream().filter(d -> d.getWord().equals(word)).count());
            }
            Long frequency = wordsFrequency.get(word);
            datas.add(new Data(data.getId(), data.getWord(), frequency == 0 ? 0 : data.getFrequency() * Math.log10(documents / wordsFrequency.get(word))));
        });
        return datas;
//        dataArray.sort((d1, d2) -> (int) Math.signum(d2.getTfIdf() - d1.getTfIdf()));
    }

    public static ArrayList<SearchResult> getReleventResults(String word, int count, ArrayList<Data> datas) {
        ArrayList<Data> array = (ArrayList<Data>) datas.stream().filter(d -> d.getWord().equals(word)).sorted(
                (d1, d2) -> (int) Math.signum(d2.getFrequency() - d1.getFrequency())
        ).limit(count).collect(Collectors.toList());
        ArrayList<SearchResult> results = new ArrayList<>();
        array.forEach(data -> results.add(new SearchResult(data.getId(), data.getFrequency())));
        return results;
    }

    public static ArrayList<SearchResult> getReleventResults(ArrayList<String> words, int count, ArrayList<Data> datas) {
        datas.sort((d1, d2) -> (int) Math.signum(d2.getFrequency() - d1.getFrequency()));
        HashMap<String, Double> hashMap = new HashMap<>();
        ArrayList<SearchResult> results = new ArrayList<>();
        datas.stream().filter(data -> words.contains(data.getWord())).forEach(data -> {
            String url = data.getId();
            if (!hashMap.keySet().contains(url)) {
                hashMap.put(url, 1 + data.getFrequency());
            } else {
                hashMap.put(url, (1 + data.getFrequency()) * hashMap.get(url));
            }
        });
        for (HashMap.Entry<String, Double> entry : hashMap.entrySet()) {
            results.add(new SearchResult(entry));
        }
        return (ArrayList<SearchResult>) results.stream().sorted(
                (r1, r2) -> (int) Math.signum(r2.getTfIdf() - r1.getTfIdf())
        ).limit(count).collect(Collectors.toList());
    }
}
