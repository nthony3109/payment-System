package com.payement.wallet.Services;

import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import com.payement.wallet.KafkaGroup.TransactionProducerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class KafkaPublisherTest {

    @Mock
    private KafkaTemplate<String, TransactionKafkaPayLoad> kafkaTemplate;

    @InjectMocks
    private TransactionProducerService transactionProducerService;

    private static final String TOPIC = "transaction-events";

    private TransactionKafkaPayLoad buildPayload(String fromAccount, String toAccount, Transactiontype type) {
        return TransactionKafkaPayLoad.builder()
                .transactionRef("TXN-001")
                .transactionAmount(BigDecimal.valueOf(5000))
                .currency(Currency.NGN)
                .description("Test transaction")
                .fromAccountNumber(fromAccount)
                .toAccountNumber(toAccount)
                .status(Status.SUCCESSFUL)
                .type(type)
                .transactionDate(LocalDateTime.now())
                .senderEmail("sender@test.com")
                .senderOldBalance(BigDecimal.valueOf(10000))
                .senderNewBalance(BigDecimal.valueOf(5000))
                .receiverEmail("receiver@test.com")
                .receiverOldBalance(BigDecimal.valueOf(2000))
                .receiverNewBalance(BigDecimal.valueOf(7000))
                .build();
    }


    // TEST 1 — Transfer uses fromAccountNumber as key
    @Test
    void shouldPublishTransferTransactionWithFromAccountAsKey() {
        TransactionKafkaPayLoad payload = buildPayload("1234567890", "0987654321", Transactiontype.TRANSFER);

        transactionProducerService.publishTransaction(payload);

        // verify Kafka was called with correct topic, key and payload
        verify(kafkaTemplate, times(1)).send(TOPIC, "1234567890", payload);
    }


    // TEST 2 — Deposit uses toAccountNumber as key (fromAccount is null)

    @Test
    void shouldPublishDepositTransactionWithToAccountAsKeyWhenFromAccountIsNull() {
        TransactionKafkaPayLoad payload = buildPayload(null, "0987654321", Transactiontype.DEPOSIT);

        transactionProducerService.publishTransaction(payload);

        // since fromAccount is null, toAccount should be used as key
        verify(kafkaTemplate, times(1)).send(TOPIC, "0987654321", payload);
    }

    // TEST 3 — Kafka is actually called (not skipped)

    @Test
    void shouldCallKafkaTemplateExactlyOnce() {
        TransactionKafkaPayLoad payload = buildPayload("1234567890", "0987654321", Transactiontype.TRANSFER);

        transactionProducerService.publishTransaction(payload);

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(TransactionKafkaPayLoad.class));
    }
}
