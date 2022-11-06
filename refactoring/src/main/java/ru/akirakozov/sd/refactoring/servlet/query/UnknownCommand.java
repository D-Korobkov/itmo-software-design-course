package ru.akirakozov.sd.refactoring.servlet.query;

public class UnknownCommand implements Command {
    private final String value;

    public UnknownCommand(String value) {
        this.value = value;
    }

    public String toHtml() {
        return String.format("Unknown command: %s", value);
    }
}
