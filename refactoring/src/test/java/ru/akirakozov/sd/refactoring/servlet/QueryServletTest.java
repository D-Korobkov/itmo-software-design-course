package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.ProductStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class QueryServletTest {

    @Test
    public void testDoGetMax() throws Exception {
        Product mostExpensiveProduct = new Product("IPhone", 700);
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ProductStorage productStorage = mock(ProductStorage.class);

        when(request.getParameter("command")).thenReturn("max");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(productStorage.getTheMostExpensiveProduct()).thenReturn(Optional.of(mostExpensiveProduct));

        new QueryServlet(productStorage).doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%n<h1>Product with max price: </h1>%nIPhone\t700</br>%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetMin() throws Exception {
        Product cheapestProduct = new Product("Xbox", 500);
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ProductStorage productStorage = mock(ProductStorage.class);

        when(request.getParameter("command")).thenReturn("min");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(productStorage.getTheCheapestProduct()).thenReturn(Optional.of(cheapestProduct));

        new QueryServlet(productStorage).doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%n<h1>Product with min price: </h1>%nXbox\t500</br>%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetSum() throws Exception {
        int summaryPrice = 1200;
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ProductStorage productStorage = mock(ProductStorage.class);

        when(request.getParameter("command")).thenReturn("sum");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(productStorage.sumProductPrices()).thenReturn(summaryPrice);

        new QueryServlet(productStorage).doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%nSummary price: %n1200%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetCount() throws Exception {
        int numberOfProducts = 2;
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ProductStorage productStorage = mock(ProductStorage.class);

        when(request.getParameter("command")).thenReturn("count");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        when(productStorage.countProducts()).thenReturn(numberOfProducts);

        new QueryServlet(productStorage).doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%nNumber of products: %n2%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetUnknownCommand() throws Exception {
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ProductStorage productStorage = mock(ProductStorage.class);

        when(request.getParameter("command")).thenReturn("UNKNOWN");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));

        new QueryServlet(productStorage).doGet(request, response);

        String expectedResponseBody = String.format("Unknown command: UNKNOWN%n");
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
}
