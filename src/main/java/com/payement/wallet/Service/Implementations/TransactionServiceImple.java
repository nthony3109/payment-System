package com.payement.wallet.Service.Implementations;

import com.payement.wallet.DTOs.*;
import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.Enum.TransferType;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Exceptions.AccountOperationException;
import com.payement.wallet.Exceptions.InsufficientFundException;
import com.payement.wallet.Exceptions.InvalidDepositAmountException;
import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import com.payement.wallet.KafkaGroup.TransactionProducerService;
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
  private final TransactionProducerService producerService;
  private  final  AccountServiceImple accountService;
  private final UserServiceImple user;

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

        BigDecimal receiverOldBalance  = accountService.checkBalance(req.getAccountNumber());
        String receiverEmail = account.getUser().getEmail();

        account.setBalance(account.getBalance().add(req.getAmount()));
        accountRepo.save(account);
        BigDecimal receiverNewBalance = accountService.checkBalance(req.getAccountNumber());
    log.info("handing over to logMethod to create transaction");
        DepositRes res = logDepositTransaction(account,req.getAmount());

        TransactionKafkaPayLoad payLoad = new TransactionKafkaPayLoad(
                res.getTransactionRef(),
                res.getAmount(),
                res.getCurrency(),
                res.getDescription(),
                null,
                res.getToAccountNumber(),
                Status.SUCCESSFUL,
                Transactiontype.DEPOSIT,LocalDateTime.now(),
                null,null,null,
                receiverEmail,
                receiverOldBalance,
                receiverNewBalance


        );

        producerService.publishTransaction(payLoad);
        log.info("deposit success and pulished to kafka");
        return res;
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

        String senderEmail = fromAccount.getUser().getEmail();
        BigDecimal senderOldBalance = accountService.checkBalance(req.getFromAccountNumber());

        String receiverEmail = toAccount.getUser().getEmail();
        BigDecimal receiverOldBalance = accountService.checkBalance(req.getToAccountNumber());

        fromAccount.setBalance(fromAccount.getBalance().subtract(req.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(req.getAmount()));
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);

        BigDecimal senderNewBalance = accountService.checkBalance(req.getFromAccountNumber());
        BigDecimal receiverNewBalance = accountService.checkBalance(req.getToAccountNumber());

        TransferRes res = logTransferTransaction(fromAccount, toAccount, req.getAmount(),req.getDescription());
        TransactionKafkaPayLoad payLoad = new TransactionKafkaPayLoad(
                res.getTransactionRef(),
                res.getAmount(),
                res.getCurrency(),
                res.getDescription(),
                req.getFromAccountNumber(),
                res.getToAccountNumber(),
                Status.SUCCESSFUL,
                Transactiontype.TRANSFER,LocalDateTime.now(),
                senderEmail,
                senderOldBalance,
                senderNewBalance,
                receiverEmail,
                receiverOldBalance,
                receiverNewBalance

        );
        producerService.publishTransaction(payLoad);
        return res;
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
    public  List<TransactionHistory> getTransactionHistoryByPage(Account account, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionCreatedAt").descending());
        return  transactionRepo.findByFromAccountOrToAccount(account, account, pageable)
                .stream().map(TransactionHistory ::new)
                .toList();

    }

}
