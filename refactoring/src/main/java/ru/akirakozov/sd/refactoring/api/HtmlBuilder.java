package ru.akirakozov.sd.refactoring.api;

import java.io.PrintWriter;
import java.util.function.Consumer;

public class HtmlBuilder {

    private final Consumer<PrintWriter> printer;

    private HtmlBuilder(Consumer<PrintWriter> printer) {
        this.printer = printer;
    }

    public void println(PrintWriter printWriter) {
        printer.andThen(PrintWriter::println).accept(printWriter);
    }

    public HtmlBuilder concat(HtmlBuilder other) {
        return new HtmlBuilder(this.printer.andThen(other.printer));
    }

    public static HtmlBuilder empty() {
        return new HtmlBuilder(printWriter -> {
        });
    }

    public static HtmlBuilder newline() {
        return new HtmlBuilder(PrintWriter::println);
    }

    public static HtmlBuilder br() {
        return raw("</br>");
    }

    public static HtmlBuilder raw(String str) {
        return new HtmlBuilder(printWriter -> printWriter.print(str));
    }

    public static HtmlBuilder html(HtmlBuilder content) {
        return tagWithContent("html", content);
    }

    public static HtmlBuilder body(HtmlBuilder content) {
        return tagWithContent("body", content);
    }

    public static HtmlBuilder h1(HtmlBuilder content) {
        return tagWithContent("h1", content);
    }

    private static HtmlBuilder tagWithContent(String tag, HtmlBuilder content) {
        return new HtmlBuilder(printWriter -> {
            printWriter.printf("<%s>", tag);
            content.printer.accept(printWriter);
            printWriter.printf("</%s>", tag);
        });
    }
}
