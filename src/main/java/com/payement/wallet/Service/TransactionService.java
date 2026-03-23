package com.payement.wallet.Service;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.Repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private  final TransactionRepo transactionRepo;
    public Transaction logDepositTransaction(Account toaccount, BigDecimal amount) {
        String transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toLowerCase();
        Transaction transaction= Transaction.builder()
                .toAccount(toaccount)
                .transactionAmount(amount)
                .currency(toaccount.getCurrency())
                .description("Deposit of " + amount + " to account " + toaccount.getAccountNumber())
                .transactionType(Transactiontype.DEPOSIT)
                .transactionRef(transactionRef)
                .fromAccount(null)
                .transactionStatus(Status.SUCCESSFUL)
                .completedAt(LocalDateTime.now())
                .build();
        return transactionRepo.save(transaction);
    }
    public Transaction logTransferTransaction(Account fromAccount, Account toAccount, BigDecimal amount, String description) {
        String transactionRef = "TNX-" + UUID.randomUUID().toString().substring(0,8).toLowerCase();
        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .transactionAmount(amount)
                .currency(fromAccount.getCurrency())
                .description(description)
                .transactionType(Transactiontype.TRANSFER)
                .transactionRef(transactionRef)
                .transactionStatus(Status.SUCCESSFUL)
                .completedAt(LocalDateTime.now())
                .build();
        return transactionRepo.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Account account) {
        List<Transaction> sentTransactions = transactionRepo.findByFromAccount(account);
        List<Transaction> receivedTransactions = transactionRepo.findByToAccount(account);
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);
        allTransactions.sort( Comparator.comparing(Transaction ::getTransactionCreatedAt).reversed());
        return allTransactions;
    }

}
