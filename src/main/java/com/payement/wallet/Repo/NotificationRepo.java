package com.payement.wallet.Repo;

import com.payement.wallet.Entity.Notification;
import com.payement.wallet.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification,Long> {
   
    List<Notification>findByUser(UserEntity user);
    List<Notification>findByUserAndIsViewed(UserEntity user);
    long coutByUserAndIsViewed(UserEntity user, boolean falseOrTrue);
}
