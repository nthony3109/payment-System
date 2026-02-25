package com.payement.wallet.Service;

import com.payement.wallet.DTOs.RegisterUserReq;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public boolean registerUser(RegisterUserReq req) {
        boolean userExistByEmail = userRepo.existsByEmail(req.getEmail());
        boolean userExistByPhoneNumber = userRepo.existsByPhoneNumber(req.getPhoneNumber());
        if (!userExistByEmail && !userExistByPhoneNumber) {
            return true;
        }
        UserEntity user = UserEntity.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .otherNames(req.getOtherNames())
                .username(req.getUsername())
                .email(req.getEmail())
                .password(req.getPassword())
                .phoneNumber(req.getPhoneNumber())
                .status(Status.UNVERIFIED)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepo.save(user) != null;
    }
}
