package com.payement.wallet.Service.Implementations;

import com.payement.wallet.DTOs.DepositRes;
import com.payement.wallet.DTOs.TransactionHistory;
import com.payement.wallet.DTOs.TransferReq;
import com.payement.wallet.DTOs.TransferRes;
import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Exceptions.AccountOperationException;
import com.payement.wallet.Exceptions.InsufficientFundException;
import com.payement.wallet.Exceptions.InvalidDepositAmountException;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.TransactionRepo;
import com.payement.wallet.Service.interfaces.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImple implements TransactionService {
  private  final TransactionRepo transactionRepo;
  private  final AccountRepo accountRepo;

    // to deposit funds
    @Override
    public DepositRes deposit (BigDecimal amount, String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("account number is not found");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositAmountException("amount must be greater than zero");
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepo.save(account);
    log.info("handing over to logMethod to create transaction");
        return logDepositTransaction(account,amount);
    }

    // to transfer funds
    @Override
    @Transactional
    public TransferRes transfer(TransferReq req) {
        // check for idempotency
        Transaction tnx = transactionRepo.findByTransactionRef(req.getTransactionRef());
        if (tnx != null) {
            return new TransferRes(tnx);
        }
        Account fromAccount = accountRepo.findByAccountNumber(req.getFromAccountNumber());
        if (fromAccount == null) {
            throw new AccountNotFoundException("sender account not found");
        }
        //to check if sender account is active
        if(fromAccount.getStatus() != Status.ACTIVE) {
            throw new AccountOperationException("sender account is not active or frozen");
        }
        Account toAccount = accountRepo.findByAccountNumber(req.getToAccountNumber());
        if (toAccount == null)
            throw new AccountNotFoundException(" receiver account not found");


        if (fromAccount.getBalance().compareTo(req.getAmount()) < 0)
            throw new InsufficientFundException("insufficient fund");


        //to check withdrawable value
        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDepositAmountException("transfer amount must be greater than zero");

        fromAccount.setBalance(fromAccount.getBalance().subtract(req.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(req.getAmount()));
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);
        return logTransferTransaction(fromAccount, toAccount, req.getAmount(),req.getDescription());
            }

   @Override
    public DepositRes logDepositTransaction(Account toaccount, BigDecimal amount) {
        log.info("inside logDepositTransaction method to create transaction");
        String transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toLowerCase();
        log.info("buiding transaction object with account {}  and amount of {}",toaccount,amount);
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
        transactionRepo.save(transaction);
        log.info("transaction object built and saved: {}", transaction);

        return new DepositRes(transaction);
    }

    @Override
    public TransferRes logTransferTransaction(Account fromAccount, Account toAccount, BigDecimal amount, String description) {
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
        transactionRepo.save(transaction);
        log.info("the transfer log transaction object is built and saved: {}", transaction);
        return new TransferRes(transaction);
    }

    @Override
    public List<TransactionHistory> getTransactionHistory(Account account) {
        List<Transaction> sentTransactions = transactionRepo.findByFromAccount(account);
        List<Transaction> receivedTransactions = transactionRepo.findByToAccount(account);
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);
        allTransactions.sort( Comparator.comparing(Transaction ::getTransactionCreatedAt).reversed());

        return allTransactions.stream()
                .map(TransactionHistory::new)
                .toList();
    }

}
