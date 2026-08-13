package com.payement.wallet.Exceptions;

public class AccountOperationException extends RuntimeException {
    public AccountOperationException(String message) {
        super(message);
    }
}
