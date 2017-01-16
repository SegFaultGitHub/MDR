import lombok.Getter;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Data {
    @Getter
    private String url;
    @Getter
    private String word;
    private int index;
    @Getter
    private double frequency;

    public Data(String url_, String word_, /*int index_,*/ double frequency_) {
        url = url_;
        word = word_;
        index = 0;
        frequency = frequency_;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\turl='" + url + '\'' +
                ",\n\tword='" + word + '\'' +
//                ",\n\tindex=" + index +
//                ",\n\ttf=" + tf +
                ",\n\tfrequency=" + frequency +
                "\n}";
    }
}
