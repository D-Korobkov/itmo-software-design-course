package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.api.HtmlBuilder;
import ru.akirakozov.sd.refactoring.api.HttpResponse;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.ProductStorage;
import ru.akirakozov.sd.refactoring.servlet.query.Command;
import ru.akirakozov.sd.refactoring.servlet.query.KnownCommand;
import ru.akirakozov.sd.refactoring.servlet.query.UnknownCommand;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import static ru.akirakozov.sd.refactoring.api.HtmlBuilder.*;

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
        Command command = Command.fromString(request.getParameter("command"));
        if (command instanceof UnknownCommand) {
            UnknownCommand unknownCommand = (UnknownCommand) command;
            HttpResponse.okHtml(response, unknownCommand.toHtml());
            return;
        }

        HtmlBuilder[] htmlBodyItems;
        try {
            htmlBodyItems = executeKnownCommand((KnownCommand) command);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HtmlBuilder htmlBody = Arrays.stream(htmlBodyItems).reduce(HtmlBuilder::concat).orElse(HtmlBuilder.empty());
        HttpResponse.okHtml(response, htmlBody);
    }

    private HtmlBuilder[] executeKnownCommand(KnownCommand command) throws SQLException {
        switch (command) {
            case max:
                return mapOptionalProduct("Product with max price: ", productStorage.getTheMostExpensiveProduct());
            case min:
                return mapOptionalProduct("Product with min price: ", productStorage.getTheCheapestProduct());
            case sum:
                return mapNumber("Summary price: ", productStorage.sumProductPrices());
            case count:
                return mapNumber("Number of products: ", productStorage.countProducts());
            default:
                throw new RuntimeException(String.format("Cannot execute the known command '%s'", command));
        }
    }

    private HtmlBuilder[] mapOptionalProduct(String title, Optional<Product> productOpt) {
        HtmlBuilder bodyItem = productOpt.map(DomainMapper::toHtmlBodyItem).orElse(HtmlBuilder.empty());
        return new HtmlBuilder[]{h1(raw(title)), newline(), bodyItem};
    }

    private HtmlBuilder[] mapNumber(String title, int number) {
        return new HtmlBuilder[]{raw(title), newline(), raw(String.valueOf(number)), newline()};
    }

}
