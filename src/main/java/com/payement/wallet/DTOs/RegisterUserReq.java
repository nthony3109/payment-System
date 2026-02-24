package com.payement.wallet.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserReq {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    private String otherNames;
    @NotEmpty
    private String Username;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String phoneNumber;
}
