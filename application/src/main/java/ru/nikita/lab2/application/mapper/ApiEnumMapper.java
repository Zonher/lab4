package ru.nikita.lab2.application.mapper;

/** Явные соответствия значений на границе слоёв; имена констант могут развиваться независимо. */
public final class ApiEnumMapper {
    private ApiEnumMapper() {}

    public static ru.nikita.lab2.domain.enumeration.Gender toDomain(
            ru.nikita.lab2.application.dto.enumeration.Gender value) {
        return switch (value) {
            case null -> null;
            case MALE -> ru.nikita.lab2.domain.enumeration.Gender.MALE;
            case FEMALE -> ru.nikita.lab2.domain.enumeration.Gender.FEMALE;
        };
    }

    public static ru.nikita.lab2.application.dto.enumeration.Gender toApi(
            ru.nikita.lab2.domain.enumeration.Gender value) {
        return switch (value) {
            case null -> null;
            case MALE -> ru.nikita.lab2.application.dto.enumeration.Gender.MALE;
            case FEMALE -> ru.nikita.lab2.application.dto.enumeration.Gender.FEMALE;
        };
    }

    public static ru.nikita.lab2.domain.enumeration.HairColor toDomain(
            ru.nikita.lab2.application.dto.enumeration.HairColor value) {
        return switch (value) {
            case null -> null;
            case BLACK -> ru.nikita.lab2.domain.enumeration.HairColor.BLACK;
            case BLONDE -> ru.nikita.lab2.domain.enumeration.HairColor.BLONDE;
            case RED -> ru.nikita.lab2.domain.enumeration.HairColor.RED;
            case COLORED -> ru.nikita.lab2.domain.enumeration.HairColor.COLORED;
        };
    }

    public static ru.nikita.lab2.application.dto.enumeration.HairColor toApi(
            ru.nikita.lab2.domain.enumeration.HairColor value) {
        return switch (value) {
            case null -> null;
            case BLACK -> ru.nikita.lab2.application.dto.enumeration.HairColor.BLACK;
            case BLONDE -> ru.nikita.lab2.application.dto.enumeration.HairColor.BLONDE;
            case RED -> ru.nikita.lab2.application.dto.enumeration.HairColor.RED;
            case COLORED -> ru.nikita.lab2.application.dto.enumeration.HairColor.COLORED;
        };
    }

    public static ru.nikita.lab2.domain.enumeration.OpType toDomain(
            ru.nikita.lab2.application.dto.enumeration.OpType value) {
        return switch (value) {
            case null -> null;
            case DEPOSIT -> ru.nikita.lab2.domain.enumeration.OpType.DEPOSIT;
            case WITHDRAW -> ru.nikita.lab2.domain.enumeration.OpType.WITHDRAW;
            case TRANSFER -> ru.nikita.lab2.domain.enumeration.OpType.TRANSFER;
        };
    }

    public static ru.nikita.lab2.application.dto.enumeration.OpType toApi(
            ru.nikita.lab2.domain.enumeration.OpType value) {
        return switch (value) {
            case null -> null;
            case DEPOSIT -> ru.nikita.lab2.application.dto.enumeration.OpType.DEPOSIT;
            case WITHDRAW -> ru.nikita.lab2.application.dto.enumeration.OpType.WITHDRAW;
            case TRANSFER -> ru.nikita.lab2.application.dto.enumeration.OpType.TRANSFER;
        };
    }
}
