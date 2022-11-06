package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.api.HttpResponse;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.ProductStorage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class AddProductServlet extends HttpServlet {

    private final ProductStorage storage;

    public AddProductServlet(ProductStorage storage) {
        this.storage = storage;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product product = new Product(
            request.getParameter("name"),
            Long.parseLong(request.getParameter("price"))
        );

        try {
            storage.insert(product);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpResponse.okHtml(response, "OK");
    }
}
