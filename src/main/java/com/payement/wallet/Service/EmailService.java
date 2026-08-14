package com.payement.wallet.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
}
