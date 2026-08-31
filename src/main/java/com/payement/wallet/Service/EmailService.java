package com.payement.wallet.Service;

import com.payement.wallet.KafkaGroup.TransactionKafkaPayLoad;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
//@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private  JavaMailSender mailSender;

    public  void sendOtpByMail (String otp, String email) {
        try {
            log.info("building the message sender objects");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("wallet account verification code");
                log.info("reading the message body(HTML body)");
            String content = """
                 <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                                        <h2 style="color: red;">Digital wallet</h2>
                                        <p>Your verification code is:</p>
                                        <div style="font-size: 28px; color: blue; font-weight: bold; letter-spacing: 4px;\s
                                                    background: grey; padding: 12px 20px; border-radius: 8px;\s
                                                    display: inline-block;">
                                            %s
                                        </div>
                                        <p style="margin-top: 16px; color: pink;">
                                            This code expires in 10 minutes. If you didn't request this,\s
                                            you can safely ignore this email.
                                        </p>
                                    </div>
                  """.formatted(otp);
            log.info("parsing  the message content to the  sender obj");
            helper.setText(content,true);
            log.info("now sending the email");
            mailSender.send(message);
            log.info("the email is sent ");
        } catch (Exception e) {
            log.info("error occurred while sending email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendDepositAlertByMail(TransactionKafkaPayLoad payLoad) {
        try {
            log.info("building the message sender objects");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(payLoad.receiverEmail());
            helper.setSubject("Deposit Successful - Wallet Credited");

            log.info("reading the message body (HTML body)");

            String content = """
                <div style="font-family: Arial, sans-serif; max-width: 520px; margin: auto;
                            background-color: whitesmoke; padding: 30px; border-radius: 10px;">

                    <h2 style="color: darkgreen; margin-bottom: 5px;">
                        Digital Wallet
                    </h2>

                    <h3 style="color: black;">
                        Deposit Successful
                    </h3>

                    <p style="color: dimgray;">
                        Your account has been successfully credited via deposit.
                    </p>

                    <div style="background-color: white; padding: 20px; border-radius: 8px;
                                margin-top: 20px;">

                        <p style="margin: 8px 0;">
                            <strong>Amount:</strong> ₦%s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Transaction Type:</strong> Deposit
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Reference:</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Date:</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Status:</strong>
                            <span style="color: darkgreen; font-weight: bold;">
                                Successful
                            </span>
                        </p>

                    </div>

                    <div style="background-color: lightgreen; padding: 15px;
                                border-radius: 8px; margin-top: 20px;">

                        <p style="margin: 0;">
                            <strong>New Balance:</strong> ₦%s
                        </p>
                        <p style="margin: 0;">
                            <strong>Old Balance:</strong> ₦%s
                        </p>

                    </div>

                    <p style="margin-top: 20px; color: dimgray; font-size: 13px;">
                        If you did not initiate or recognize this transaction,
                        please contact our support team immediately.
                    </p>

                    <p style="color: dimgray;">
                        Regards,<br>
                        <strong>Digital Wallet Team</strong>
                        <br>
                          <i> powered by T-primeFinance </i>
                        </br>
                    </p>

                </div>
                """.formatted(
                        payLoad.transactionAmount(),
                    payLoad.transactionRef(),
                    payLoad.transactionDate(),
                    payLoad.receiverNewBalance(),
                    payLoad.receiverOldBalance()
            );

            log.info("parsing the message content to the sender object");

            helper.setText(content, true);

            log.info("now sending the email");

            mailSender.send(message);

            log.info("the deposit alert email is sent");

        } catch (Exception e) {
            log.error("error occurred while sending deposit alert email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void sendCreditMail(TransactionKafkaPayLoad payLoad) {
        if ( payLoad.receiverEmail() == null)
            return;

        try {
            log.info("building the message sender objects");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(payLoad.receiverEmail());
            helper.setSubject("credit alert");

            log.info("reading the message body (HTML body)");

            String content = """
                <div style="font-family: Arial, sans-serif; max-width: 520px; margin: auto;
                            background-color: whitesmoke; padding: 30px; border-radius: 10px;">

                    <h2 style="color: darkblue; margin-bottom: 5px;">
                        Digital Wallet
                    </h2>

                    <h3 style="color: black;">
                        account deposit
                    </h3>

                    <p style="color: dimgray;">
                        Your account is credited successfully
                    </p>

                    <div style="background-color: white; padding: 20px; border-radius: 8px;
                                margin-top: 20px;">

                        <p style="margin: 8px 0;">
                            <strong>Amount:</strong> ₦%s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Transaction Type:</strong> Transfer
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>from :</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Reference:</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Date:</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Status:</strong>
                            <span style="color: darkgreen; font-weight: bold;">
                                Successful
                            </span>
                        </p>

                    </div>

                    <div style="background-color: lightyellow; padding: 15px;
                                border-radius: 8px; margin-top: 20px;">

                        <p style="margin: 0;">
                            <strong>New Balance:</strong> ₦%s
                        </p>
                        <p style="margin: 0;">
                            <strong>oldBalance:</strong> ₦%s
                        </p>

                    </div>

                    <p style="color: dimgray;">
                        Regards,<br>
                        <strong>Digital Wallet Team</strong>
                        <br>
                          <i> powered by T-primeFinance </i>
                        </br>
                    </p>

                </div>
                """.formatted(
                    payLoad.transactionAmount(),
                    payLoad.fromAccountNumber(),
                    payLoad.transactionRef(),
                    payLoad.transactionDate(),
                    payLoad.receiverNewBalance(),
                    payLoad.receiverNewBalance()
            );

            log.info("parsing the message content to the sender object");

            helper.setText(content, true);

            log.info("now sending the email");

            mailSender.send(message);

            log.info("the transfer alert email is sent");

        } catch (Exception e) {
            log.error("error occurred while sending transfer alert email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendDebitMail(TransactionKafkaPayLoad payLoad) {
        try {
            log.info("building the message sender objects");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(payLoad.senderEmail());
            helper.setSubject("Transfer Successful - Wallet Debit Alert");

            log.info("reading the message body (HTML body)");

            String content = """
                <div style="font-family: Arial, sans-serif; max-width: 520px; margin: auto;
                            background-color: whitesmoke; padding: 30px; border-radius: 10px;">

                    <h2 style="color: darkblue; margin-bottom: 5px;">
                        Digital Wallet
                    </h2>

                    <h3 style="color: black;">
                        Transfer Successful
                    </h3>

                    <p style="color: dimgray;">
                        Your transfer has been successfully processed.
                    </p>

                    <div style="background-color: white; padding: 20px; border-radius: 8px;
                                margin-top: 20px;">

                        <p style="margin: 8px 0;">
                            <strong>Amount:</strong> ₦%s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Transaction Type:</strong> Transfer
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Recipient:</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Reference:</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Date:</strong> %s
                        </p>

                        <p style="margin: 8px 0;">
                            <strong>Status:</strong>
                            <span style="color: darkgreen; font-weight: bold;">
                                Successful
                            </span>
                        </p>

                    </div>

                    <div style="background-color: lightyellow; padding: 15px;
                                border-radius: 8px; margin-top: 20px;">

                        <p style="margin: 0;">
                            <strong>New Balance:</strong> ₦%s
                        </p>
                        <p style="margin: 0;">
                            <strong>Old Balance:</strong> ₦%s
                        </p>

                    </div>

                    <p style="margin-top: 20px; color: dimgray; font-size: 13px;">
                        If you did not authorize this transaction,
                        please contact our support team immediately.
                    </p>

                    <p style="color: dimgray;">
                        Regards,<br>
                        <strong>Digital Wallet Team</strong>
                        <br>
                          <i> powered by T-primeFinance </i>
                        </br>
                    </p>

                </div>
                """.formatted(
                    payLoad.transactionAmount(),
                    payLoad.toAccountNumber(),
                    payLoad.transactionRef(),
                    payLoad.transactionDate(),
                    payLoad.senderNewBalance(),
                    payLoad.senderNewBalance()
            );

            log.info("parsing the message content to the sender object");

            helper.setText(content, true);

            log.info("now sending the email");

            mailSender.send(message);

            log.info("the transfer alert email is sent");

        } catch (Exception e) {
            log.error("error occurred while sending transfer alert email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
