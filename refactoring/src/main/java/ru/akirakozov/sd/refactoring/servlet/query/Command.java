package ru.akirakozov.sd.refactoring.servlet.query;

public interface Command {

    static Command fromString(String str) {
        try {
            return KnownCommand.valueOf(str);
        } catch (IllegalArgumentException ignored) {
            return new UnknownCommand(str);
        }
    }

}

