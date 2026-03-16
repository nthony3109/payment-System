package com.payement.wallet.Exceptions;

public class InsufficientFundException extends RuntimeException {
    public InsufficientFundException(String message) {
        super(message);
        System.err.println("omo ur money no reach oo, the amount no work ");
    }
}
