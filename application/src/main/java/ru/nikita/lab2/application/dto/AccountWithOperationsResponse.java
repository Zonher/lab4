package ru.nikita.lab2.application.dto;

import java.util.List;

public record AccountWithOperationsResponse(
        AccountResponse account,
        List<OperationResponse> operations
) {
}
