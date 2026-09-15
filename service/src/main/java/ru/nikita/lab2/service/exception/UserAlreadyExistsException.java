package ru.nikita.lab2.service.exception;

public class UserAlreadyExistsException extends ServiceException {
    public UserAlreadyExistsException(String login) {
        super("Login already exists: " + login);
    }
}
