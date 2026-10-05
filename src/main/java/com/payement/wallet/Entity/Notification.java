package com.payement.wallet.Entity;

import com.payement.wallet.Enum.Transactiontype;
import com.payement.wallet.Enum.TransferType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private  String message;
    private String messageTittle;
    private boolean isViewed;

    @Enumerated(EnumType.STRING)
    private Transactiontype notificationType;
    private String transactionRef;
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "account_id",referencedColumnName = "id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @CreationTimestamp
    private LocalDateTime createdAt;
}