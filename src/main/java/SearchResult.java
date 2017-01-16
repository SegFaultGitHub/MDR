import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * Created by SegFault on 14/01/2017.
 */
public class SearchResult {
    @Getter
    private String url;
    @Getter @Setter
    private double tfIdf;

    public SearchResult(String url, double tfIdf) {
        this.url = url;
        this.tfIdf = tfIdf;
    }

    public SearchResult(HashMap.Entry<String, Double> entry) {
        this.url = entry.getKey();
        this.tfIdf = entry.getValue();
    }

    @Override
    public String toString() {
        return "{" +
                "url='" + url + '\'' +
                ", tfIdf=" + tfIdf +
                '}';
    }
}
