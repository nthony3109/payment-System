package com.payement.wallet.Entity;

import com.payement.wallet.Enum.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String otherNames;
    @NotBlank
    private  String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotEmpty
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createdAt;
    @CreationTimestamp
    private LocalDateTime updatedAt;
    @OneToMany
    private Account account;

}
