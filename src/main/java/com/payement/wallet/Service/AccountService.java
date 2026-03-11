package com.payement.wallet.Service;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Exceptions.InsufficientFundException;
import com.payement.wallet.Repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;

    public String generateAccountNumber(String phoneNumber) {
        String accountNumber;
        String trimPhoneNumber = phoneNumber.replaceAll("\\D+", "").trim()
                ;
        if (trimPhoneNumber.startsWith("234")) {
            accountNumber = trimPhoneNumber.substring(3);
        }
        else {
            accountNumber = trimPhoneNumber.substring(1);

        }
        return accountNumber;
    }

    public boolean deposit (BigDecimal amount, String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("account number is not found");
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


}
