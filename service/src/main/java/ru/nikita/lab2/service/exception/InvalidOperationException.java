package ru.nikita.lab2.service.exception;

public class InvalidOperationException extends ServiceException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
