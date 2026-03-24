package com.payement.wallet.Events;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Enum.Transactiontype;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class TransactionEvent extends ApplicationEvent  {
    private final Account fromAccount;
    private final Account toAccount;
    private final BigDecimal amount;
    private final Transactiontype transactionType;

    //my  constructor class as @RequiredArgConstructor does not work here
    public TransactionEvent(Object source, Account fromAccount, BigDecimal amount,
                            Account toAccount, Transactiontype transactionType) {
        super(source);
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionType = transactionType;
        this.amount = amount;
    }

}
