package com.payement.wallet.Services;

import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import com.payement.wallet.Service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;


    private TransactionKafkaPayLoad buildPayload(String receiverEmail, String senderEmail) {
        return TransactionKafkaPayLoad.builder()
                .transactionRef("TXN-001")
                .transactionAmount(BigDecimal.valueOf(5000))
                .currency(Currency.NGN)
                .description("Test transaction")
                .fromAccountNumber("1234567890")
                .toAccountNumber("0987654321")
                .status(Status.SUCCESSFUL)
                .type(Transactiontype.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .senderEmail(senderEmail)
                .senderOldBalance(BigDecimal.valueOf(10000))
                .senderNewBalance(BigDecimal.valueOf(5000))
                .receiverEmail(receiverEmail)
                .receiverOldBalance(BigDecimal.valueOf(2000))
                .receiverNewBalance(BigDecimal.valueOf(7000))
                .build();
    }

    @BeforeEach
    void set() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendOtpMail() {
        emailService.sendOtpByMail("00-11-11","devtony74@gmail.com");
        verify(mailSender,times(1)).createMimeMessage();
        verify(mailSender,times(1)).send(mimeMessage);
    }


    @Test
    void shouldThrowRuntimeExceptionWhenOtpEmailFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server down"));

        assertThrows(RuntimeException.class, () ->
                emailService.sendOtpByMail("123456", "devtony74@gmail.com")
        );
    }

    @Test
    void shouldSendDepositAlertEmailSuccessfully() {
        TransactionKafkaPayLoad payload = buildPayload("devtony74@gmail.com", null);

        emailService.sendDepositAlertByMail(payload);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDepositAlertFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server down"));
        TransactionKafkaPayLoad payload = buildPayload("devtony74@gmail.com", null);

        assertThrows(RuntimeException.class, () ->
                emailService.sendDepositAlertByMail(payload)
        );
    }

    // CREDIT ALERT TESTS


    @Test
    void shouldSendCreditAlertEmailSuccessfully() {
        TransactionKafkaPayLoad payload = buildPayload("devtony74@gmail.com", "devtony74@gmail.com");

        emailService.sendCreditMail(payload);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void shouldSkipCreditAlertWhenReceiverEmailIsNull() {
        TransactionKafkaPayLoad payload = buildPayload(null, "devtony74@gmail.com");

        emailService.sendCreditMail(payload);

        // mailSender should never be called since receiverEmail is null
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenCreditAlertFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server down"));
        TransactionKafkaPayLoad payload = buildPayload("devtony74@gmail.com", "devtony74@gmail.com");

        assertThrows(RuntimeException.class, () ->
                emailService.sendCreditMail(payload)
        );
    }


    // DEBIT ALERT TESTS


    @Test
    void shouldSendDebitAlertEmailSuccessfully() {
        TransactionKafkaPayLoad payload = buildPayload("devtony74@gmail.com", "devtony74@gmail.com");

        emailService.sendDebitMail(payload);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDebitAlertFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server down"));
        TransactionKafkaPayLoad payload = buildPayload("devtony74@gmail.com", "devtony74@gmail.com");

        assertThrows(RuntimeException.class, () ->
                emailService.sendDebitMail(payload)
        );
    }

}
