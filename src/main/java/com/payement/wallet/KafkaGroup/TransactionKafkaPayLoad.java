package com.payement.wallet.KafkaGroup;

import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionKafkaPayLoad(
        String transactionRef,
        BigDecimal transactionAmount,
        Currency currency,
        String description,
        String fromAccountNumber,
        String toAccountNumber,
        Status status,
        Transactiontype type,
        LocalDateTime completedAt
) {
}
