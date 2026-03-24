package com.payement.wallet.Entity;

import com.payement.wallet.Enum.Currency;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Enum.Transactiontype;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true,nullable = false)
    private String transactionRef;
    private BigDecimal transactionAmount;

    @Enumerated(EnumType.STRING)
    private Currency currency;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Enumerated(EnumType.STRING)
    private Status transactionStatus;
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private Transactiontype transactionType;
    @CreationTimestamp
    private LocalDateTime TransactionCreatedAt;



}
