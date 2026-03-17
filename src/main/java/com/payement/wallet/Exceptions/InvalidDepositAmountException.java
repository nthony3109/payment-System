package com.payement.wallet.Exceptions;

public class InvalidDepositAmountException extends RuntimeException{
    public InvalidDepositAmountException(String message) {
        super(message); // to access the runtime exception class
    }
}
