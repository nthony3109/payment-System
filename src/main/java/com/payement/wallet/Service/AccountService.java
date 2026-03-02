package com.payement.wallet.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    public String createAccountNumber(String phoneNumber) {
        String accountNumber;
        String trimPhoneNumber = phoneNumber.replaceAll("\\D+", "").trim()
                ;
        if (trimPhoneNumber.startsWith("234")) {
            accountNumber = trimPhoneNumber.substring(3);
        }
        else {
            accountNumber = trimPhoneNumber.substring(1);

        }
        return accountNumber;
    }
}
