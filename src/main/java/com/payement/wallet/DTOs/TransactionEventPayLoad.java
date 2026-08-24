package com.payement.wallet.DTOs;

import com.payement.wallet.Enum.Transactiontype;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionEventPayLoad {

    public  record  TransactionEventPayLoadDTO(
        Long fromAccountId;
        Long toAccountId;
        BigDecimal amount;
        Transactiontype transactionType;
            Instant timestamp;

    ) {}
}
