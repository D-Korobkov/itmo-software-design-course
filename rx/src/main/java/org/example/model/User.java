package org.example.model;

import org.bson.Document;

import java.util.Map;

public record User(String login, Currency currency) {

    public Document toDocument() {
        return new Document(Map.of(
            "login", login,
            "currency", currency.name()
        ));
    }

    public static User fromDocument(Document doc) {
        return new User(
            doc.getString("login"),
            Currency.valueOf(doc.getString("currency"))
        );
    }

    public static Document loginFilter(String login) {
        return new Document(Map.of(
            "login", login
        ));
    }
}
