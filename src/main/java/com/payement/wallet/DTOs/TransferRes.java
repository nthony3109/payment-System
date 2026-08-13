package com.payement.wallet.DTOs;

import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
public class TransferRes {
    private String transactionRef;
    private BigDecimal amount;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String description;
    private Status transctionStatus;
    private LocalDateTime completedAt;
    private Currency currency;
    private Transactiontype transactiontype;


    public TransferRes(Transaction transaction) {
    }
}
