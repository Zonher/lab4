package ru.nikita.lab2.service.exception;

public class InsufficientFundsException extends ServiceException {
    public InsufficientFundsException(java.util.UUID id) {
        super("Insufficient funds in account: " + id);
    }
}
