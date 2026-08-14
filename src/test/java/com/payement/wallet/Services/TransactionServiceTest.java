package com.payement.wallet.Services;

import com.payement.wallet.DTOs.DepositReq;
import com.payement.wallet.DTOs.DepositRes;
import com.payement.wallet.Entity.Account;
import com.payement.wallet.Entity.Transaction;
import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Repo.AccountRepo;
import com.payement.wallet.Repo.TransactionRepo;
import com.payement.wallet.Service.Implementations.TransactionServiceImple;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class TransactionServiceTest {
    @InjectMocks
    private TransactionServiceImple tnxService;
    @Mock
    private AccountRepo acctRepo;
    @Mock
    private TransactionRepo tnxRepo;



    @Test
    void  depositTest() {
        log.info("initializing deposit request");
        String acctNum = "7017996873";
        Account acct = new Account();
        acct.setAccountNumber(acctNum);
        acct.setBalance(BigDecimal.ZERO);

        BigDecimal amount = new BigDecimal(1000);
        log.info("attempting to deposit {} into account number {}", amount, acctNum);
        when(acctRepo.findByAccountNumber(acctNum)).thenReturn(acct);
        DepositRes deposited = tnxService.deposit(DepositReq.builder().accountNumber(acctNum).amount(amount).transactionRef("txn_ref_1").build());
        assert deposited != null;
        log.info("deposit successful, transaction reference: {}", deposited.getTransactionRef());
        verify(acctRepo).findByAccountNumber(acctNum);






    }

    @Test
    void logDepositTnx() {
        Account acct = new Account();
                acct.setAccountNumber("7017996873");
                acct.setBalance(BigDecimal.ZERO);
                acct.setCurrency(Currency.NGN);
        BigDecimal amount = new BigDecimal(1000);
        when(tnxRepo.save(any(Transaction.class))).thenAnswer(invocation ->  invocation.getArgument(0, Transaction.class));
        log.info("caling method to create log");
        DepositRes tnx = tnxService.logDepositTransaction(acct, amount);
        log.info("transaction logged: {}", tnx);
        assert tnx != null;
        log.info("logged deposit transaction: {}", tnx.getTransactionRef());
        verify(tnxRepo).save(any(Transaction.class));

    }
}
