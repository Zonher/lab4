package ru.nikita.lab2.application.mapper;

import org.springframework.stereotype.Component;

import ru.nikita.lab2.application.dto.*;
import ru.nikita.lab2.domain.Account;
import ru.nikita.lab2.domain.FieldPatch;
import ru.nikita.lab2.domain.Operation;
import ru.nikita.lab2.domain.User;
import ru.nikita.lab2.domain.UserUpdate;

import java.util.UUID;

@Component
public class ApiMapper {
    public User toDomain(UserCreateRequest request) {
        return new User(
                null,
                request.login(),
                request.name(),
                request.age(),
                ApiEnumMapper.toDomain(request.gender()),
                ApiEnumMapper.toDomain(request.hairColor()));
    }

    public User toDomain(UUID userId, UserUpdateRequest request) {
        return new User(
                userId,
                null,
                request.name(),
                request.age(),
                ApiEnumMapper.toDomain(request.gender()),
                ApiEnumMapper.toDomain(request.hairColor()));
    }

    public UserUpdate toDomain(UUID id, UserPatchRequest request) {
        return new UserUpdate(
                id,
                request.name(),
                request.age(),
                request.gender().present()
                        ? FieldPatch.of(ApiEnumMapper.toDomain(request.gender().value()))
                        : FieldPatch.absent(),
                request.hairColor().present()
                        ? FieldPatch.of(ApiEnumMapper.toDomain(request.hairColor().value()))
                        : FieldPatch.absent());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.login(),
                user.name(),
                user.age(),
                ApiEnumMapper.toApi(user.gender()),
                ApiEnumMapper.toApi(user.hairColor()));
    }

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(account.id(), account.userId(), account.balance());
    }

    public OperationResponse toResponse(Operation operation) {
        return new OperationResponse(
                operation.id(),
                operation.accountId(),
                operation.destinationId(),
                ApiEnumMapper.toApi(operation.opType()),
                operation.amount(),
                operation.commission(),
                operation.operationInstant());
    }
}
