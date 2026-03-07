package com.payement.wallet.Repo;

import com.payement.wallet.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account,Long> {
    Account findByAccountNumber(String accountNumber);
}
