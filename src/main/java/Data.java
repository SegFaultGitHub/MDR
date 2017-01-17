import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Data {
    @Getter
    private String url;
    @Getter
    private String word;
    @Getter
    private double frequency;
    @Getter
    private ArrayList<Integer> positions;

    public Data(String url_, String word_, /*int index_,*/ double frequency_, ArrayList<Integer> positions_) {
        url = url_;
        word = word_;
        frequency = frequency_;
        positions = positions_;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\turl='" + url + '\'' +
                ",\n\tword='" + word + '\'' +
                ",\n\tfrequency=" + frequency +
                ",\n\tpositions=" + positions +
                "\n}";
    }
}
