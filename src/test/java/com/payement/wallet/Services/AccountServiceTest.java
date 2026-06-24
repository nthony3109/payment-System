package com.payement.wallet.Services;


import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Service.AccountService;
import com.payement.wallet.Service.TransactionService;
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
    @InjectMocks
    private TransactionService tnxService;

    @Test
    void testCreateAccountNumber(){
        String phoneNumber = "2347017996873";
        String createdAccountNumber = accountService.generateAccountNumber(phoneNumber);
        System.out.println(createdAccountNumber);
        assert createdAccountNumber.equals("7017996873");
    }


}
