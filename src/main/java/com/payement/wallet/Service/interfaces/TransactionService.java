package com.payement.wallet.Service.interfaces;

import com.payement.wallet.DTOs.DepositRes;
import com.payement.wallet.DTOs.TransactionHistory;
import com.payement.wallet.DTOs.TransferReq;
import com.payement.wallet.DTOs.TransferRes;
import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    DepositRes deposit (BigDecimal amount, String accountNumber);
    TransferRes transfer(TransferReq req);
    DepositRes logDepositTransaction(Account toaccount, BigDecimal amount);
    TransferRes logTransferTransaction(Account fromAccount, Account toAccount, BigDecimal amount, String description);
    List<TransactionHistory> getTransactionHistory(Account account);
}
