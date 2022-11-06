package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.api.HtmlBuilder;
import ru.akirakozov.sd.refactoring.api.HttpResponse;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.ProductStorage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {
    private final ProductStorage productStorage;

    public GetProductsServlet(ProductStorage productStorage) {
        this.productStorage = productStorage;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> allProducts;
        try {
            allProducts = productStorage.getAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HtmlBuilder htmlBody = allProducts.stream().reduce(
            HtmlBuilder.empty(),
            (acc, product) -> acc.concat(DomainMapper.toHtmlBodyItem(product)),
            HtmlBuilder::concat
        );
        HttpResponse.okHtml(response, htmlBody);
    }
}
