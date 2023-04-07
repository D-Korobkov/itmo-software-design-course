package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.ResponseContentWriter;
import org.example.bank.CurrencyRateService;
import org.example.bank.MockCurrencyRateService;
import org.example.database.ReactiveMongoDriver;
import org.example.model.Currency;
import org.example.model.Good;
import org.example.model.User;


public class RxNettyHttpServer {
    private static final String DB_CONN_STRING = "mongodb://user:password@localhost:27017/rxdb";

    public static void main(final String[] args) {
        CurrencyRateService rateService = new MockCurrencyRateService();

        try (final ReactiveMongoDriver db = new ReactiveMongoDriver(DB_CONN_STRING)) {
            HttpServer
                .newServer(8080)
                .start((req, resp) -> {

                    switch (req.getDecodedPath()) {
                        case "/user":
                            if (req.getHttpMethod() == HttpMethod.POST) {
                                return handleNewUser(req, resp, db);
                            }
                            return resp.setStatus(HttpResponseStatus.NOT_FOUND);
                        case "/good":
                            if (req.getHttpMethod() == HttpMethod.POST) {
                                return handleNewGood(req, resp, db);
                            }
                            return resp.setStatus(HttpResponseStatus.NOT_FOUND);
                        case "/price-list":
                            if (req.getHttpMethod() == HttpMethod.GET) {
                                return handleGetPriceList(req, resp, db, rateService);
                            }
                            return resp.setStatus(HttpResponseStatus.NOT_FOUND);
                        default:
                            return resp.setStatus(HttpResponseStatus.NOT_FOUND);
                    }
                })
                .awaitShutdown();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

    }

    private static ResponseContentWriter<ByteBuf> handleNewUser(
        HttpServerRequest<ByteBuf> req,
        HttpServerResponse<ByteBuf> resp,
        ReactiveMongoDriver db
    ) {
        var params = req.getQueryParameters();
        var login = params.get("login");
        var currencyStr = params.get("currency");
        if (login.size() != 1 || currencyStr.size() != 1) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }

        Currency currency;
        try {
            currency = Currency.valueOf(currencyStr.get(0));
        } catch (IllegalArgumentException e) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }

        return resp.writeString(db.insertUser(new User(login.get(0), currency)).map(Object::toString));
    }

    private static ResponseContentWriter<ByteBuf> handleNewGood(
        HttpServerRequest<ByteBuf> req,
        HttpServerResponse<ByteBuf> resp,
        ReactiveMongoDriver db
    ) {
        var params = req.getQueryParameters();
        var name = params.get("name");
        var priceRubStr = params.get("priceRub");
        if (name.size() != 1 || priceRubStr.size() != 1) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }

        double priceRub;
        try {
            priceRub = Double.parseDouble(priceRubStr.get(0));
        } catch (NumberFormatException e) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }

        return resp.writeString(db.insertGood(new Good(name.get(0), priceRub)).map(Object::toString));
    }

    private static ResponseContentWriter<ByteBuf> handleGetPriceList(
        HttpServerRequest<ByteBuf> req,
        HttpServerResponse<ByteBuf> resp,
        ReactiveMongoDriver db,
        CurrencyRateService rateService
    ) {
        var params = req.getQueryParameters();
        var userLogin = params.get("userLogin");
        if (userLogin.size() != 1) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }

        var priceList = db.findUser(userLogin.get(0)).flatMap((user) -> {
            double rate = rateService.getRate(Currency.RUB, user.currency());

            return db.findAllGoods().map((good) ->
                String.format("%s: %.2f %s%n", good.name(), good.priceRub() * rate, user.currency().name())
            );
        });
        return resp.writeString(priceList);
    }
}
