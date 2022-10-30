package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.ProductStorage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    private final ProductStorage productStorage;

    public QueryServlet(ProductStorage productStorage) {
        this.productStorage = productStorage;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter responseWriter = response.getWriter();
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            try {
                Optional<Product> productOpt = productStorage.getTheMostExpensiveProduct();

                responseWriter.println("<html><body>");
                responseWriter.println("<h1>Product with max price: </h1>");
                productOpt.ifPresent(product ->
                    responseWriter.println(product.getName() + "\t" + product.getPrice() + "</br>")
                );
                responseWriter.println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("min".equals(command)) {
            try {
                Optional<Product> productOpt = productStorage.getTheCheapestProduct();

                responseWriter.println("<html><body>");
                responseWriter.println("<h1>Product with min price: </h1>");
                productOpt.ifPresent(product ->
                    responseWriter.println(product.getName() + "\t" + product.getPrice() + "</br>")
                );
                responseWriter.println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("sum".equals(command)) {
            try {
                int summaryPrice = productStorage.sumProductPrices();

                responseWriter.println("<html><body>");
                responseWriter.println("Summary price: ");
                responseWriter.println(summaryPrice);
                responseWriter.println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("count".equals(command)) {
            try {
                int numberOfProducts = productStorage.countProducts();

                responseWriter.println("<html><body>");
                responseWriter.println("Number of products: ");
                responseWriter.println(numberOfProducts);
                responseWriter.println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            responseWriter.println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
