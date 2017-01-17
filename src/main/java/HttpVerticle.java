import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.jsoup.nodes.Document;

/**
 * Created by SegFault on 17/01/2017.
 */
public class HttpVerticle extends AbstractVerticle {
    private int port;

    public HttpVerticle(int port) {
        this.port = port;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/api/crawl/:url").handler(context -> {
            String url = context.request().getParam("url");
//            val cleansed = if (url.matches(Constants.HTTP_REGEXP)) url else "http://$url"
            vertx.eventBus().send("/api/crawl", "https://" + url + "/");
            context.response().end("Started crawl of root URL " + url);
        });

        server.requestHandler(router::accept).listen(port);
    }
}
