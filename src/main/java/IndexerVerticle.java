import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by SegFault on 17/01/2017.
 */
public class IndexerVerticle extends AbstractVerticle {
    public Indexer indexer;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        indexer = new Indexer(vertx.eventBus());
    }

    public void index(CustomDocument document) {
        indexer.index(document);
    }
}
