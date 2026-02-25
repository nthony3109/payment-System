package com.payement.wallet.Repo;

import com.payement.wallet.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserEntity,Long> {
        boolean existsByEmail(String email);
        boolean existsByUsername(String username);
        boolean existsByPhoneNumber(String phoneNumber);
}
