package ru.nikita.lab2.service.exception;

public class NoAccountFoundException extends ServiceException {
    public NoAccountFoundException(java.util.UUID id) {
        super("Account not found: " + id);
    }
}
