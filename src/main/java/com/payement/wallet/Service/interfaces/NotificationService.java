package com.payement.wallet.Service.interfaces;

import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Notification;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {
    public Page<Notification> getAllNotification(String accountNumber,int page, int size);
    public long countReadAndUnreadmessages(String accountNumber, boolean readOrUnread);
    public List<Notification> FilterNotification(String AccountNumber, boolean trueOrFalse);
    public void sendDepositNotification(TransactionKafkaPayLoad payLoad);
    public void sendCreditNotification(TransactionKafkaPayLoad payLoad);
    public void sendDebitNotification(TransactionKafkaPayLoad payLoad);

}
