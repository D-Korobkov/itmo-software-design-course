package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.api.HtmlBuilder;
import ru.akirakozov.sd.refactoring.model.Product;

import static ru.akirakozov.sd.refactoring.api.HtmlBuilder.*;

public class DomainMapper {

    public static HtmlBuilder toHtmlBodyItem(Product product) {
        return raw(product.getName())
            .concat(raw("\t"))
            .concat(raw(String.valueOf(product.getPrice())))
            .concat(br())
            .concat(newline());
    }

}
