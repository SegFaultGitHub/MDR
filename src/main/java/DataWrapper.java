import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by SegFault on 17/01/2017.
 */
public class DataWrapper {
    @Getter
    private ArrayList<Data> datas;

    public DataWrapper(ArrayList<Data> datas) {
        this.datas = datas;
    }

    public void add(Data data) {
        datas.add(data);
    }
}
