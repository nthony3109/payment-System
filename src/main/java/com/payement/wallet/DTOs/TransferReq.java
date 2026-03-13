package com.payement.wallet.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
@Getter
public class TransferReq {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}
