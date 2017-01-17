import javafx.util.Pair;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by SegFault on 16/01/2017.
 */
public class Indexable {
    private static ArrayList<String> stopWords;
    private static HashMap<String, String> lemmas;

    public static void initialize() {
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileReader.close();
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileReader.close();
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String cleanUp(String clean) {
        clean = clean.toLowerCase().replaceAll("[^a-z0-9\\-']+", " ");
        return clean;
    }

    protected HashMap<String, Pair<Integer, ArrayList<Integer>>> reduceAndTokenize(String input) {
        HashMap<String, Pair<Integer, ArrayList<Integer>>> lemmasCount = new HashMap<>();
        ArrayList<String> cleanSplit = new ArrayList<>();
        Collections.addAll(cleanSplit, input.split(" +"));
        for (int i = 0, j = 0; i < cleanSplit.size(); i++, j++) {
            String word = cleanSplit.get(i);
            String lemma = getLemma(word);
            if (stopWords.contains(lemma)) {
                j--;
                continue;
            }
            if (lemmasCount.containsKey(lemma)) {
                lemmasCount.put(
                        lemma,
                        new Pair<>(lemmasCount.get(lemma).getKey() + 1, lemmasCount.get(lemma).getValue())
                );
            } else {
                lemmasCount.put(
                        lemma,
                        new Pair<>(1, new ArrayList<>())
                );
            }
            lemmasCount.get(lemma).getValue().add(j);
        }
        return lemmasCount;
    }

    protected ArrayList<Data> fillDataArray(String url, HashMap<String, Pair<Integer, ArrayList<Integer>>> lemmasCount) {
        ArrayList<Data> datasArray = new ArrayList<>();
        double D = 0;
        for (String key : lemmasCount.keySet()) {
            D += lemmasCount.get(key).getKey() * lemmasCount.get(key).getKey();
        }
        D = Math.sqrt(D);
        for (String key : lemmasCount.keySet()) {
            datasArray.add(new Data(
                    url,
                    key,
                    (double) lemmasCount.get(key).getKey() / D,
                    lemmasCount.get(key).getValue()
            ));
        }
        return datasArray;
    }

    protected ArrayList<Data> getDatas(String url, String input) {
        return fillDataArray(url, reduceAndTokenize(cleanUp(input)));
    }

    protected String getLemma(String word) {
        if (lemmas.containsKey(word)) {
            return lemmas.get(word);
        } else {
            return word;
        }
    }
}
