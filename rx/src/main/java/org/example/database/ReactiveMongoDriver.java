package org.example.database;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import org.example.model.Good;
import org.example.model.User;
import rx.Observable;

import java.util.Map;

public class ReactiveMongoDriver implements AutoCloseable {
    private static final String MONGO_DATABASE = "rxdb";
    private static final String USER_TABLE = "users";
    private static final String GOOD_TABLE = "goods";

    private final MongoClient client;

    public ReactiveMongoDriver(String connectionString) throws InterruptedException {
        client = MongoClients.create(connectionString);
    }

    public Observable<User> findUser(String login) {
        return client.getDatabase(MONGO_DATABASE).getCollection(USER_TABLE)
            .find(User.loginFilter(login))
            .first()
            .map(User::fromDocument);
    }

    public Observable<Success> insertUser(User user) {
        return client.getDatabase(MONGO_DATABASE).getCollection(USER_TABLE)
            .insertOne(user.toDocument());
    }

    public Observable<Good> findAllGoods() {
        return client.getDatabase(MONGO_DATABASE).getCollection(GOOD_TABLE)
            .find()
            .toObservable()
            .map(Good::fromDocument);
    }

    public Observable<Success> insertGood(Good good) {
        return client.getDatabase(MONGO_DATABASE).getCollection(GOOD_TABLE)
            .insertOne(good.toDocument());
    }

    @Override
    public void close() {
        client.close();
    }
}
