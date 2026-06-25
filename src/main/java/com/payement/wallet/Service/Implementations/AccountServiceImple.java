package com.payement.wallet.Service.Implementations;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Exceptions.InsufficientFundException;
import com.payement.wallet.Exceptions.InvalidPhoneNumberException;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.TransactionRepo;
import com.payement.wallet.Service.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImple implements AccountService {
    private final AccountRepo accountRepo;
    private  final TransactionServiceImple transactionService;
    private final TransactionRepo transactionRepo;

    //to create account for new users
    @Override
    public Account createAccount(UserEntity user) {
        String phoneNumber = user.getPhoneNumber();
        String accountNumber = generateAccountNumber(phoneNumber);
        if (accountRepo.findByAccountNumber(accountNumber) != null) {
            throw new InvalidPhoneNumberException("phone number  already exist ");
        }
        Account account = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency(Currency.NGN)
                .build();
        return accountRepo.save(account);
    }

    // to generate account number
    @Override
    public String generateAccountNumber(String phoneNumber) {
        String accountNumber;
        String trimPhoneNumber = phoneNumber.replaceAll("\\D+", "").trim();
        if (trimPhoneNumber.length() < 11 ||  trimPhoneNumber.length() > 15) {
            throw new InvalidPhoneNumberException("phone number must be between 10 and 15");
        }
        if (trimPhoneNumber.startsWith("234")) {
            accountNumber = trimPhoneNumber.substring(3);
        }
        else {
            accountNumber = trimPhoneNumber.substring(1);

        }
        return accountNumber;
    }

    @Override
    public Account getAccountByUser(UserEntity user) {
        return accountRepo.findByUser(user)
                .orElseThrow(
                        () -> new AccountNotFoundException("account not found for the user")
                );
    }

     // to get account by account number
    @Override
    public Account getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("account number is not found");
        }
        return account;
    }

    // to fetch balance
    @Override
    public BigDecimal checkBalance(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("no account found with the account number");
        }
        return account.getBalance();
    }

}
