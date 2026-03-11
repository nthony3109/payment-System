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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;


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

    public Account getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("account number is not found");
        }
        return account;
    }

    public boolean deposit (BigDecimal amount, String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("account number is not found");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositAmountException("amount must be greater than zero");
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepo.save(account);
        return true;
    }

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

    public BigDecimal checkBalance(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("no account found with the account number");
        }
        return account.getBalance();
    }

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
        return fromAccount;
    }


}
