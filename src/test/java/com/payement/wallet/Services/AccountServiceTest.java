package com.payement.wallet.Services;


import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Service.Implementations.AccountServiceImple;
import com.payement.wallet.Service.Implementations.TransactionServiceImple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private AccountServiceImple accountService;
    @Mock
    private AccountRepo accountRepo;
    @InjectMocks
    private TransactionServiceImple tnxService;

    @Test
    void testCreateAccountNumber(){
        String phoneNumber = "2347017996873";
        String createdAccountNumber = accountService.generateAccountNumber(phoneNumber);
        System.out.println(createdAccountNumber);
        assert createdAccountNumber.equals("7017996873");
    }


}
