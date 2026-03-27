package com.payement.wallet.Service;

import com.payement.wallet.DTOs.TransferReq;
import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Exceptions.InsufficientFundException;
import com.payement.wallet.Exceptions.InvalidDepositAmountException;
import com.payement.wallet.Exceptions.InvalidPhoneNumberException;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private  final TransactionService transactionService;
    private final TransactionRepo transactionRepo;

    //to create account for new users
    public Account createAccount(UserEntity user) {
        String phoneNumber = user.getPhoneNumber();
        String accountNumber = generateAccountNumber(phoneNumber);
        Account account = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency(Currency.NGN)
                .build();
        return accountRepo.save(account);
    }

    // to generate account number
    public String generateAccountNumber(String phoneNumber) {
        String accountNumber;
        String trimPhoneNumber = phoneNumber.replaceAll("\\D+", "").trim();
        if (trimPhoneNumber.length() < 11 ||  trimPhoneNumber.length() > 15) {
            throw new InvalidPhoneNumberException("phone number must be between 9 and 15");
        }
        if (trimPhoneNumber.startsWith("234")) {
            accountNumber = trimPhoneNumber.substring(3);
        }
        else {
            accountNumber = trimPhoneNumber.substring(1);

        }
        return accountNumber;
    }

    public Account getAccountByUser(UserEntity user) {
        return accountRepo.findByUser(user)
                .orElseThrow(
                        () -> new AccountNotFoundException("account not found for the user")
                );
    }

     // to get account by account number
    public Account getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("account number is not found");
        }
        return account;
    }

    // to withdraw funds
    public boolean withdraw(BigDecimal amount, String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("no account found with the account number");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundException("insufficient fund");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepo.save(account);
        return true;
    }
    // to fetch balance
    public BigDecimal checkBalance(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("no account found with the account number");
        }
        return account.getBalance();
    }





}
