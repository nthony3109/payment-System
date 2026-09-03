package com.payement.wallet.KafkaGroup;

import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.Service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionConsumer {
    private  final NotificationService notificationService;

    @KafkaListener(
            topics = "transaction-events",
            groupId = "notification-group"
    )
    public void consume(TransactionKafkaPayLoad payLoad) {
        if (payLoad.type().equals(Transactiontype.DEPOSIT)) {
            log.info("event received with transaction Ref : {}, now routing to notification service ",payLoad.transactionRef());
            notificationService.sendDepositNotification(payLoad);
            return;
        }

        notificationService.sendDebitNotification(payLoad);

        if (payLoad.receiverEmail() == null) {
            log.info("the receiver seems to be an external email is null therefore the credit alert won't be sent, receiver account number is : {} ", payLoad.toAccountNumber());
            return;
        }
        
        notificationService.sendCreditNotification(payLoad);

    }
}
