package org.example.model;

import org.bson.Document;

import java.util.Map;

public record Good(String name, double priceRub) {

    public Document toDocument() {
        return new Document(Map.of(
            "name", name,
            "priceRub", priceRub
        ));
    }

    public static Good fromDocument(Document doc) {
        return new Good(
            doc.getString("name"),
            doc.getDouble("priceRub")
        );
    }
}
