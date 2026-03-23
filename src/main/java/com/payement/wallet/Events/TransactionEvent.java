package com.payement.wallet.Events;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Enum.Transactiontype;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class TransactionEvent extends ApplicationEvent  {
    private final Account fromAccount;
    private final Account toAccount;

    public TransactionEvent(Object source, Account fromAccount, Account toAccount, BigDecimal amount, Transactiontype transactionType) {
        super(source);
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    private BigDecimal amount;
    private final Transactiontype transactionType;
    public TransactionEvent(Object source) {
        super(source);
    }

}
