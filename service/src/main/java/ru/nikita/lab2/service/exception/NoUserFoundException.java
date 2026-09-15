package ru.nikita.lab2.service.exception;

public class NoUserFoundException extends ServiceException {
    public NoUserFoundException(java.util.UUID id) {
        super("User not found: " + id);
    }
}
