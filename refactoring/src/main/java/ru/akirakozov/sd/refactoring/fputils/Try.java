package ru.akirakozov.sd.refactoring.fputils;

public class Try<E extends Throwable, V> {
    private final V value;
    private final E error;

    private Try(V value, E error) {
        this.value = value;
        this.error = error;
    }

    public V getValue() throws E {
        if (error != null) {
            throw error;
        }
        return value;
    }

    public static <E extends Throwable, V> Try<E, V> success(V value) {
        return new Try<>(value, null);
    }

    public static <E extends Throwable, V> Try<E, V> failure(E error) {
        return new Try<>(null, error);
    }
}
