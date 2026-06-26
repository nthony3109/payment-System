package com.payement.wallet.Service.Implementations;

import com.payement.wallet.DTOs.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public DepositRes deposit (DepositReq req) {
        //check first if the transaction reference already exists to ensure idempotency
        Transaction tnx = transactionRepo.findByTransactionRef(req.getTransactionRef());
        if (tnx != null ) {
            return new DepositRes(tnx);
        }
        Account account = accountRepo.findByAccountNumber(req.getAccountNumber());
        if (account == null) {
            throw new AccountNotFoundException("account number is not found");
        }
        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositAmountException("amount must be greater than zero");
        }
        account.setBalance(account.getBalance().add(req.getAmount()));
        accountRepo.save(account);
    log.info("handing over to logMethod to create transaction");
        return logDepositTransaction(account,req.getAmount());
    }

    // to transfer/withdraw funds
    @Override
    @Transactional
    public TransferRes transfer(TransferReq req) {
        // check for idempotency
        Transaction tnx = transactionRepo.findByTransactionRef(req.getTransactionRef());
        if (tnx != null) {
            return new TransferRes(tnx);
        }
        // to check to ensure that transfer are not made to same account
        if (req.getToAccountNumber().equals(req.getFromAccountNumber()))
            throw new AccountOperationException("transfer cannot be made to same account");

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

     //to  log the deposit activities
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

    //to log the Transfer activities
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

    // to get all transaction activities
    @Override
    @Deprecated
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

    // to return paginated transaction history
    @Override
    public  List<TransactionHistory> getTransactionHistory(Account account, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionCreatedAt").descending());
        return  transactionRepo.findByFromAccountOrToAccount(account, account, pageable)
                .stream().map(TransactionHistory ::new)
                .toList();

    }

}
