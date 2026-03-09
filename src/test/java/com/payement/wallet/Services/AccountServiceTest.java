package com.payement.wallet.Services;


import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private  AccountService accountService;
    @Mock
    private AccountRepo accountRepo;

    @Test
    void testCreateAccountNumber(){
        String phoneNumber = "2347017996873";
        String createdAccountNumber = accountService.createAccountNumber(phoneNumber);
        System.out.println(createdAccountNumber);
        assert createdAccountNumber.equals("7017996873");
    }

    @Test
    void depositTest(){
        System.out.println("deposit test");
        System.out.println("deposit test");
        String accountNumber = "7017996873";
        BigDecimal amount = new BigDecimal(1000);
        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(null);
        System.out.println("attempting to deposit " + amount + " into account number " + accountNumber);
        boolean result = accountService.deposit(amount, accountNumber);
        assert result;
        verify(accountRepo.findByAccountNumber(accountNumber));
    }
}
