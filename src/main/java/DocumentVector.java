import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by SegFault on 16/01/2017.
 */
public class DocumentVector {
    @Getter
    private String url;
    private ArrayList<Double> vector;

    public DocumentVector(String url_, ArrayList<Double> vector_) {
        url = url_;
        vector = vector_;
    }

    public double dotProduct(DocumentVector documentVector) {
        if (vector.size() != documentVector.vector.size()) {
            return -1;
        }
        double result = 0;
        for (int i = 0; i < vector.size(); i++) {
            result += vector.get(i) * documentVector.vector.get(i);
        }
        return result;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\turl='" + url + '\'' +
                ",\n\tvector=" + vector +
                "\n}";
    }
}
