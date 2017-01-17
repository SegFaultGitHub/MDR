import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by SegFault on 17/01/2017.
 */
public class IndexVerticle extends AbstractVerticle {
    private Queue<String> urlsCrawled;
    private Queue<Data> datas;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
    }

    public void addData(ArrayList<Data> dataToAdd) {
        datas.addAll(dataToAdd);
    }
}
