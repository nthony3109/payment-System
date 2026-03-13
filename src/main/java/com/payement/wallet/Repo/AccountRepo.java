package com.payement.wallet.Repo;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account,Long> {
    Account findByAccountNumber(String accountNumber);
    Optional<Account> findByUser(UserEntity user);
}
