package com.payement.wallet.Exceptions;

public class InvalidTransferAmount extends RuntimeException {
    public InvalidTransferAmount(String message) {
        super(message);
    }
}
