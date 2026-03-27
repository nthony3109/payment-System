package com.payement.wallet.Service;

import com.payement.wallet.DTOs.TransferReq;
import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Exceptions.InsufficientFundException;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.TransactionRepo;
import jakarta.transaction.Transactional;
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
  private  final AccountRepo accountRepo;

    // to transfer funds
    @Transactional
    public Account transfer(TransferReq req) {
        Account fromAccount = accountRepo.findByAccountNumber(req.getFromAccountNumber());
        if (fromAccount == null) {
            throw new AccountNotFoundException("sender account not found");
        }
        Account toAccount = accountRepo.findByAccountNumber(req.getToAccountNumber());
        if (toAccount == null) {
            throw new AccountNotFoundException(" receiver account not found");
        }
        if (fromAccount.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundException("insufficient fund");
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(req.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(req.getAmount()));
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);
        logTransferTransaction(fromAccount, toAccount, req.getAmount(),req.getDescription());
        return fromAccount;
    }

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
