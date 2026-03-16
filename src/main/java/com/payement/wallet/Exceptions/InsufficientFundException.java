package com.payement.wallet.Exceptions;

public class InsufficientFundException extends RuntimeException {
    public InsufficientFundException(String message) {
        super(message);
    }
}
