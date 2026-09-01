package com.payement.wallet.Services;

import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.KafkaGroup.TransactionConsumer;
import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import com.payement.wallet.Service.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class KafkaConsumerTest {
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionConsumer transactionConsumer;

    private TransactionKafkaPayLoad buildPayload(Transactiontype type, String receiverEmail) {
        return TransactionKafkaPayLoad.builder()
                .transactionRef("TXN-001")
                .transactionAmount(BigDecimal.valueOf(5000))
                .currency(Currency.NGN)
                .description("Test transaction")
                .fromAccountNumber("1234567890")
                .toAccountNumber("0987654321")
                .status(Status.SUCCESSFUL)
                .type(type)
                .transactionDate(LocalDateTime.now())
                .senderEmail("sender@test.com")
                .senderOldBalance(BigDecimal.valueOf(10000))
                .senderNewBalance(BigDecimal.valueOf(5000))
                .receiverEmail(receiverEmail)
                .receiverOldBalance(BigDecimal.valueOf(2000))
                .receiverNewBalance(BigDecimal.valueOf(7000))
                .build();
    }


    // TEST 1 — Deposit only calls sendDepositNotification

    @Test
    void shouldCallSendDepositNotificationForDepositTransaction() {
        TransactionKafkaPayLoad payload = buildPayload(Transactiontype.DEPOSIT, "receiver@test.com");

        transactionConsumer.consume(payload);

        verify(notificationService, times(1)).sendDepositNotification(payload);
        verify(notificationService, never()).sendDebitNotification(payload);
        verify(notificationService, never()).sendCreditNotification(payload);
    }

    // TEST 2 — Internal transfer notifies both sender and receiver

    @Test
    void shouldCallBothDebitAndCreditNotificationForInternalTransfer() {
        TransactionKafkaPayLoad payload = buildPayload(Transactiontype.TRANSFER, "receiver@test.com");

        transactionConsumer.consume(payload);

        verify(notificationService, times(1)).sendDebitNotification(payload);
        verify(notificationService, times(1)).sendCreditNotification(payload);
        verify(notificationService, never()).sendDepositNotification(payload);
    }


    // TEST 3 — External transfer only notifies sender (receiver email is null)

    @Test
    void shouldOnlyCallDebitNotificationForExternalTransfer() {
        TransactionKafkaPayLoad payload = buildPayload(Transactiontype.TRANSFER, null);

        transactionConsumer.consume(payload);

        verify(notificationService, times(1)).sendDebitNotification(payload);
        verify(notificationService, never()).sendCreditNotification(payload);
        verify(notificationService, never()).sendDepositNotification(payload);
    }


    // TEST 4 — Deposit never calls debit or credit
    @Test
    void shouldNeverCallDebitOrCreditForDeposit() {
        TransactionKafkaPayLoad payload = buildPayload(Transactiontype.DEPOSIT, "receiver@test.com");

        transactionConsumer.consume(payload);

        verify(notificationService, never()).sendDebitNotification(any());
        verify(notificationService, never()).sendCreditNotification(any());
    }
}
