package com.payement.wallet.Service.Implementations;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Notification;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.Enum.TransferType;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.NotificationRepo;
import com.payement.wallet.Repo.UserRepo;
import com.payement.wallet.Service.EmailService;
import com.payement.wallet.Service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImple implements NotificationService {
    private final NotificationRepo repo;
    private final AccountRepo accountRepo;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    // to get user for notification operation
    private Account getAccount(String accountNumber){
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("account not found to fetch notifications");
        }
        return account;
    }

    // notification Builder to reduce boilerplate codes
    private Notification notificationBuilder(String accountNumber, Transactiontype type, String message, String messageTittle) {
        return Notification.builder()
                .messageTittle(messageTittle)
                .notificationType(type)
                .message(message)
                .createdAt(LocalDateTime.now())
                .account(getAccount(accountNumber))
                .isViewed(false)
                .build();
    }

    @Override
    public Page<Notification> getAllNotification(String accountNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return repo.findByAccount(getAccount(accountNumber),pageable);
    }
    //readOrUnread must be true for read messages and false for unread messages

    @Override
    public List<Notification> FilterNotification(String accountNumber, boolean trueOrFalse) {
        return repo.findByAccountAndIsViewed(getAccount(accountNumber),trueOrFalse);
    }

    //continue with the count method to return read and unread messages number
    @Override
    public long countReadAndUnreadmessages(String accountNumber, boolean trueOrFalse) {
        return
                repo.countByAccountAndIsViewed(getAccount(accountNumber),
                        trueOrFalse);
    }

    //send notification section
    @Override
    public  void sendDepositNotification(TransactionKafkaPayLoad payLoad) {
        String messageTittle = "Deposit Alert";
        String message = " you received  NGN"+ payLoad.transactionAmount() + "from " + payLoad.toAccountNumber();
        Notification notification = notificationBuilder(payLoad.fromAccountNumber(), payLoad.type(),message,messageTittle);
        repo.save(notification);
        // message to websocket
        messagingTemplate.convertAndSendToUser(payLoad.toAccountNumber(),"/queue/notifications", notification);

        log.info(" websocket DEPOSIT ALERT notification pushed, now handing over to email service to send deposit received mail");
        emailService.sendDepositAlertByMail(payLoad);

    }
    @Override
    public void sendDebitNotification(TransactionKafkaPayLoad payLoad) {
        String messageTittle = "Debit Alert";
        String message = "you transferred NGN" + payLoad.transactionAmount() + " to " + payLoad.fromAccountNumber();
        Notification notification = notificationBuilder(payLoad.toAccountNumber(), payLoad.type(), message, messageTittle);
        repo.save(notification);
        messagingTemplate.convertAndSendToUser(payLoad.fromAccountNumber(),"/queue/notificatins",notification);
        log.info("websocket DEBIT ALERT notification pushed, nowhanding over to email service to send debit mail");
        emailService.sendDebitMail(payLoad);

    }

    @Override
    public  void sendCreditNotification(TransactionKafkaPayLoad payLoad) {

        String messageTittle = "Credit Alert";
        String message = " you received  NGN"+ payLoad.transactionAmount() + "from " + payLoad.fromAccountNumber();
        Notification notification = notificationBuilder(payLoad.toAccountNumber(), payLoad.type(), message,messageTittle);
        repo.save(notification);
        messagingTemplate.convertAndSendToUser(payLoad.toAccountNumber(),"queue/notifications", notification);
        log.info(" webscoket CREDIT ALERT notification pushed, now handing over to email service to send credit mail");
        emailService.sendCreditMail(payLoad);

    }




}
