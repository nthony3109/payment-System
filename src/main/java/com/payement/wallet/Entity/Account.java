package com.payement.wallet.Entity;

import com.payement.wallet.Enum.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private long transactionPin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private Currency currency;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Version
    private Long version;

    @OneToMany(mappedBy = "fromAccount",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List <Transaction> fromAccountTransaction = new ArrayList<>();
    @OneToMany(mappedBy = "toAccount",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List <Transaction> toAccountTransaction = new ArrayList<>();
}
