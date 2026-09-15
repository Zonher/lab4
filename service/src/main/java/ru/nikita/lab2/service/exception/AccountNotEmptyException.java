package ru.nikita.lab2.service.exception;

public class AccountNotEmptyException extends ServiceException {
    public AccountNotEmptyException(java.util.UUID id) {
        super("Account balance must be zero: " + id);
    }
}
