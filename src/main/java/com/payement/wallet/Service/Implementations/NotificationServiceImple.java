package com.payement.wallet.Service.Implementations;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Notification;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Events.TransactionEvent;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.NotificationRepo;
import com.payement.wallet.Repo.UserRepo;
import com.payement.wallet.Service.EmailService;
import com.payement.wallet.Service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImple implements NotificationService {
    private final NotificationRepo repo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final EmailService emailService;

    // to get user for notification operation
    private UserEntity getUser(String accountNumber){
        Account account = accountRepo.findByAccountNumber(accountNumber);
        UserEntity user = account.getUser();
        if (user == null) {
            throw new AccountNotFoundException("account not found to fetch notifications");
        }
        return user;
    }

    @Override
    public List<Notification> getAllNotification(String accountNumber) {

        return repo.findByUser(getUser(accountNumber));
    }
    //readOrUnread must be true for read messages and false for unread messages

    @Override
    public List<Notification> FilterNotification(String AccountNumber, boolean readOrUnread) {
        return repo.findByUserAndIsViewed(getUser(AccountNumber),readOrUnread);
    }

    //continue with the count method to return read and unread messages number
    @Override
    public long countReadAndUnreadmessages(UserEntity user, boolean readOrUnread) {
        return
                repo.countByUserAndIsViewed(getUser("accountNumber"),
                        readOrUnread);
    }

    //send notification section
    @Override
    public  void sendDepositNotification(TransactionKafkaPayLoad payLoad) {
        log.info("handing over to email service to send deposit received mail");
        emailService.sendDepositAlertByMail(payLoad);

    }
    @Override
    public void sendDebitNotification(TransactionKafkaPayLoad payLoad) {
        log.info("handing over to email service to send debit mail");
        emailService.sendDebitMail(payLoad);

    }

    @Override
    public  void sendCreditNotification(TransactionKafkaPayLoad payLoad) {
        log.info("handing over to email service to send credit mail");
        emailService.sendCreditMail(payLoad);

    }




}
