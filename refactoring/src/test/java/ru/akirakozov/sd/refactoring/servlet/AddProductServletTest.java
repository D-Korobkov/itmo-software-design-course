package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.ProductStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class AddProductServletTest {
    @Test
    public void testDoGet() throws Exception {
        Product product = new Product("Xbox", 666);
        StringWriter responseBodyWriter = new StringWriter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ProductStorage productStorage = mock(ProductStorage.class);

        when(request.getParameter("name")).thenReturn(product.getName());
        when(request.getParameter("price")).thenReturn(String.valueOf(product.getPrice()));
        when(response.getWriter()).thenReturn(new PrintWriter(responseBodyWriter));
        doNothing().when(productStorage).insert(any());

        new AddProductServlet(productStorage).doGet(request, response);

        Assertions.assertEquals(String.format("OK%n"), responseBodyWriter.toString());

        verify(request, times(1)).getParameter("name");
        verify(request, times(1)).getParameter("price");
        verify(productStorage, times(1)).insert(product);
        verify(response, times(1)).setContentType("text/html");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

}
