package ru.akirakozov.sd.refactoring.api;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpResponse {

    public static void okHtml(HttpServletResponse response, String html) throws IOException {
        response.getWriter().println(html);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static void okHtml(HttpServletResponse response, HtmlBuilder htmlBody) throws IOException {
        HtmlBuilder html = HtmlBuilder.html(
            HtmlBuilder.body(
                HtmlBuilder.newline().concat(htmlBody)
            )
        );

        html.println(response.getWriter());
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
