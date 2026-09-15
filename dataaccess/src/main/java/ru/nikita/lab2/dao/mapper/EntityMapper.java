package ru.nikita.lab2.dao.mapper;

import ru.nikita.lab2.dao.entity.*;
import ru.nikita.lab2.domain.*;

public final class EntityMapper {
    private EntityMapper() {}

    public static User user(UserEntity e) {
        return new User(
                e.getId(),
                e.getLogin(),
                e.getName(),
                e.getAge(),
                EntityEnumMapper.toDomain(e.getGender()),
                EntityEnumMapper.toDomain(e.getHairColor()));
    }

    public static Account account(AccountEntity e) {
        return new Account(e.getId(), e.getUser().getId(), e.getBalance());
    }

    public static Operation operation(OperationEntity e) {
        return new Operation(
                e.getId(),
                e.getAccount().getId(),
                e.getDestination() == null ? null : e.getDestination().getId(),
                EntityEnumMapper.toDomain(e.getOpType()),
                e.getAmount(),
                e.getCommission(),
                e.getOperationAt());
    }

    public static AuthAccount authAccount(AuthAccountEntity e){
        return new AuthAccount(
                e.getId(),
                e.getLogin(),
                e.getPasswordHash(),
                EntityEnumMapper.toDomain(e.getRole()),
                e.getUser() == null ? null : e.getUser().getId(),
                e.isEnabled()
        );
    }
}
