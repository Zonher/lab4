package ru.nikita.lab2.service.exception;

public class InvalidAmountException extends ServiceException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
