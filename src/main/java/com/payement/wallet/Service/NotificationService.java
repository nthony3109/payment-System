package com.payement.wallet.Service;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Notification;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.NotificationRepo;
import com.payement.wallet.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepo repo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;

    private UserEntity getUser(String accountNumber){
        Account account = accountRepo.findByAccountNumber(accountNumber);
        UserEntity user = account.getUser();
        if (user == null) {
            throw new AccountNotFoundException("account not find to fetch notifications");
        }
        return user;
    }

    public List<Notification> getAllNotification(String accountNumber) {

        return repo.findByUser(getUser(accountNumber));
    }
    public List<Notification> FilterNotification(String AccounntNumber) {
        return repo.findByUserAndIsViewed(getUser(AccounntNumber));
    }
    //continue with the count method to return readed and un read messages number
}
