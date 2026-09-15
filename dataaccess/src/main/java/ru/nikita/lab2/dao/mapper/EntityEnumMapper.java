package ru.nikita.lab2.dao.mapper;

/** Явные соответствия значений на границе слоёв; имена констант могут развиваться независимо. */
public final class EntityEnumMapper {
    private EntityEnumMapper() {}

    public static ru.nikita.lab2.domain.enumeration.Gender toDomain(
            ru.nikita.lab2.dao.entity.enumeration.Gender value) {
        return switch (value) {
            case null -> null;
            case MALE -> ru.nikita.lab2.domain.enumeration.Gender.MALE;
            case FEMALE -> ru.nikita.lab2.domain.enumeration.Gender.FEMALE;
        };
    }

    public static ru.nikita.lab2.dao.entity.enumeration.Gender toEntity(
            ru.nikita.lab2.domain.enumeration.Gender value) {
        return switch (value) {
            case null -> null;
            case MALE -> ru.nikita.lab2.dao.entity.enumeration.Gender.MALE;
            case FEMALE -> ru.nikita.lab2.dao.entity.enumeration.Gender.FEMALE;
        };
    }

    public static ru.nikita.lab2.domain.enumeration.HairColor toDomain(
            ru.nikita.lab2.dao.entity.enumeration.HairColor value) {
        return switch (value) {
            case null -> null;
            case BLACK -> ru.nikita.lab2.domain.enumeration.HairColor.BLACK;
            case BLONDE -> ru.nikita.lab2.domain.enumeration.HairColor.BLONDE;
            case RED -> ru.nikita.lab2.domain.enumeration.HairColor.RED;
            case COLORED -> ru.nikita.lab2.domain.enumeration.HairColor.COLORED;
        };
    }

    public static ru.nikita.lab2.dao.entity.enumeration.HairColor toEntity(
            ru.nikita.lab2.domain.enumeration.HairColor value) {
        return switch (value) {
            case null -> null;
            case BLACK -> ru.nikita.lab2.dao.entity.enumeration.HairColor.BLACK;
            case BLONDE -> ru.nikita.lab2.dao.entity.enumeration.HairColor.BLONDE;
            case RED -> ru.nikita.lab2.dao.entity.enumeration.HairColor.RED;
            case COLORED -> ru.nikita.lab2.dao.entity.enumeration.HairColor.COLORED;
        };
    }

    public static ru.nikita.lab2.domain.enumeration.OpType toDomain(
            ru.nikita.lab2.dao.entity.enumeration.OpType value) {
        return switch (value) {
            case null -> null;
            case DEPOSIT -> ru.nikita.lab2.domain.enumeration.OpType.DEPOSIT;
            case WITHDRAW -> ru.nikita.lab2.domain.enumeration.OpType.WITHDRAW;
            case TRANSFER -> ru.nikita.lab2.domain.enumeration.OpType.TRANSFER;
        };
    }

    public static ru.nikita.lab2.dao.entity.enumeration.OpType toEntity(
            ru.nikita.lab2.domain.enumeration.OpType value) {
        return switch (value) {
            case null -> null;
            case DEPOSIT -> ru.nikita.lab2.dao.entity.enumeration.OpType.DEPOSIT;
            case WITHDRAW -> ru.nikita.lab2.dao.entity.enumeration.OpType.WITHDRAW;
            case TRANSFER -> ru.nikita.lab2.dao.entity.enumeration.OpType.TRANSFER;
        };
    }

    public static ru.nikita.lab2.domain.enumeration.Role toDomain(
            ru.nikita.lab2.dao.entity.enumeration.Role value
    ){
        return switch (value){
            case null -> null;
            case ADMIN -> ru.nikita.lab2.domain.enumeration.Role.ADMIN;
            case CLIENT -> ru.nikita.lab2.domain.enumeration.Role.CLIENT;
        };
    }

    public static ru.nikita.lab2.dao.entity.enumeration.Role toEntity(
            ru.nikita.lab2.domain.enumeration.Role value
    ){
        return switch (value){
            case null -> null;
            case ADMIN -> ru.nikita.lab2.dao.entity.enumeration.Role.ADMIN;
            case CLIENT -> ru.nikita.lab2.dao.entity.enumeration.Role.CLIENT;
        };
    }
}
