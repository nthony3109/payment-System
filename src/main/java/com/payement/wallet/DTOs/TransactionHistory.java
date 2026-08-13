package com.payement.wallet.DTOs;

import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class TransactionHistory {
    private String toAccountNumber;
    private BigDecimal amount;
    private String transactionRef;
    private String description;
    private Status transctionStatus;
    private LocalDateTime completedAt;
    private Currency currency;
    private Transactiontype transactiontype;

    public TransactionHistory(List<Transaction> allTransactions) {
    }

    public TransactionHistory(Transaction transaction) {
    }
}
