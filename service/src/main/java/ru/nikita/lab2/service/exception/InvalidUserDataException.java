package ru.nikita.lab2.service.exception;

public class InvalidUserDataException extends ServiceException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
