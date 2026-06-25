package com.payement.wallet.Service.interfaces;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.UserEntity;

import java.math.BigDecimal;

public interface AccountService {
    Account createAccount(UserEntity user);
    String generateAccountNumber(String phoneNumber);
    Account getAccountByUser(UserEntity user);
    Account getAccountByAccountNumber(String accountNumber);
    BigDecimal checkBalance(String accountNumber);
}
