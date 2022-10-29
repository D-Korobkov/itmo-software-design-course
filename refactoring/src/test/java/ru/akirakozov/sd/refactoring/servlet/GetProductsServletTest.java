package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.ProductStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class GetProductsServletTest {

    @Test
    public void testDoGet() throws Exception {
        List<Product> allProducts = Arrays.asList(new Product("Xbox", 500), new Product("IPhone", 700));

        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ProductStorage productStorage = mock(ProductStorage.class);

        when(productStorage.getAll()).thenReturn(allProducts);
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));

        new GetProductsServlet(productStorage).doGet(request, response);

        String expectedResponseBody = String.format(
            "<html><body>%nXbox\t500</br>%nIPhone\t700</br>%n</body></html>%n"
        );
        Assertions.assertEquals(expectedResponseBody, responseBodyWriter.toString());

        verify(productStorage, times(1)).getAll();
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }


}
