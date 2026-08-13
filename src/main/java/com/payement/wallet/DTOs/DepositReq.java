package com.payement.wallet.DTOs;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class DepositReq {
    private String accountNumber;
    private String transactionRef;
    private BigDecimal amount;
    private String description;
}
