import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by SegFault on 16/01/2017.
 */
public class Manager {
    private static Logger logger;

    private final int THREADS = 10;

    public Manager() {
        logger = LogManager.getLogger(Manager.class);

        Crawler.initialize();
        Indexer.initialize();
        Indexable.initialize();
        Engine.initialize();
    }

    public void executeWithVerticles() {
        VertxOptions options = new VertxOptions();
        options.setBlockedThreadCheckInterval(1000 * 60 * 60);
        Vertx vertx = Vertx.vertx(options);

        vertx.eventBus().registerDefaultCodec(Document.class, new DocumentCodec());
        vertx.eventBus().registerDefaultCodec(DataWrapper.class, new DataWrapperCodec());

        HttpVerticle httpVerticle = new HttpVerticle(8080);
        vertx.deployVerticle(httpVerticle);

        for (int i = 0; i < THREADS; i++) {
            CrawlerVerticle crawlerVerticle = new CrawlerVerticle();
            vertx.deployVerticle(crawlerVerticle);
            MessageConsumer<String> consumerUrls = vertx.eventBus().consumer("/api/crawl");
            consumerUrls.handler(message ->
                    crawlerVerticle.crawl(message.body())
            );

            IndexerVerticle indexerVerticle = new IndexerVerticle();
            vertx.deployVerticle(indexerVerticle);
            MessageConsumer<CustomDocument> consumerDocument = vertx.eventBus().consumer("/api/index");
            consumerDocument.handler(message ->
                    indexerVerticle.index(message.body())
            );
        }

        IndexVerticle indexVerticle = new IndexVerticle();
        vertx.deployVerticle(indexVerticle);
        MessageConsumer<ArrayList<Data>> consumerAddData = vertx.eventBus().consumer("/api/addData");
        consumerAddData.handler(message ->
                indexVerticle.addData(message.body())
        );
    }
}
