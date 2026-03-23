package com.payement.wallet.Repo;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction>findByFromAccount(Account account);
    List<Transaction>findByToAccount(Account account);
    Transaction findByTransactionRef(String ref);
    List<Transaction> findByTransactionType(Enum transactionType);
}
