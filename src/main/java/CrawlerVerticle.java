import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by SegFault on 17/01/2017.
 */
public class CrawlerVerticle extends AbstractVerticle {
    private Crawler crawler;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        crawler = new Crawler(vertx.eventBus());
    }

    public void crawl(String url) {
        crawler.crawl(url);
    }
}
