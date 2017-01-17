import io.vertx.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by Thomas VENNER on 13/01/2017.
 */
public class Indexer extends Indexable {
    private static Logger logger;

    public static void initialize() {
        logger = LogManager.getLogger(Indexer.class);
    }

    private EventBus eventBus;

    public Indexer(EventBus eventBus_) {
        eventBus = eventBus_;
    }

    public void index(CustomDocument document) {
        if (document != null) {
            logger.info("Indexing {}", document.getLocation());
            if (document.getBodyText() == null) return;
            eventBus.send("/api/addData", new DataWrapper(getDatas(document.getLocation(), document.getBodyText())));
        } else {
            logger.error("Document null");
        }
    }
}
