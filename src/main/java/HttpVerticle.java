import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

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

        router.get("/search/:query").handler((context) -> {
            String query = context.request().getParam("query");
            context.response().end("Hello Worlds! : " + query);
        });

        server.requestHandler(router::accept).listen(port);
    }
}
