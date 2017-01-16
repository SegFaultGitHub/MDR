import lombok.Getter;
import lombok.Setter;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Data {
    @Getter
    private String id;
    @Getter
    private String word;
    private int index;
    @Getter
    private double frequency;

    public Data(String id_, String word_, /*int index_,*/ double frequency_) {
        id = id_;
        word = word_;
        index = 0;
        frequency = frequency_;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\tid='" + id + '\'' +
                ",\n\tword='" + word + '\'' +
//                ",\n\tindex=" + index +
//                ",\n\ttf=" + tf +
                ",\n\tfrequency=" + frequency +
                "\n}";
    }
}
