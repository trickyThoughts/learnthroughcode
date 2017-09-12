package ai.devops.reactive.launcher;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;

public class EndPointConfiguratorVerticle extends AbstractVerticle {

    private WebClient webClient;

    @Override
    public void start() {

        webClient = WebClient.create(vertx);
        Router router  = Router.router(vertx);
        router.get("/").handler(this::invokeCATRetriever);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(9091);

    }

    private void invokeCATRetriever(RoutingContext routingContext) {
        HttpRequest<JsonObject> request = webClient
                .get(80, "cat.eng.vmware.com", "/api/v3.0/testrun")
                .setQueryParam("username", "svc.e2eautouser")
                .setQueryParam("api_key", "98a390e6eda5ee13268f5dc4d6b4e8a78cba9b29&site=26")
                .setQueryParam("site_id", "26")
                .setQueryParam("limit", "100")
                .setQueryParam("order_by", "-end_time")
                .setQueryParam("result!", "PASS")
                .as(BodyCodec.jsonObject());

        Single<JsonObject> catResponse = request.rxSend().map(HttpResponse::body);
        catResponse.subscribe( result -> {
                        routingContext.response().end(result.encodePrettily());


                    },
                    error -> {
                        error.printStackTrace();
                        routingContext.response()
                                .setStatusCode(500).end(error.getMessage());
                    });

    }

}
