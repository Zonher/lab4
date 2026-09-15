package ru.nikita.lab2.domain;

/** Отличает отсутствующее поле PATCH от явно переданного null. */
public record FieldPatch<T>(boolean present, T value) {
    public static <T> FieldPatch<T> absent() {
        return new FieldPatch<>(false, null);
    }

    public static <T> FieldPatch<T> of(T value) {
        return new FieldPatch<>(true, value);
    }
}
