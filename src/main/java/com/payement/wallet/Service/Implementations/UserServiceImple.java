package com.payement.wallet.Service.Implementations;

import com.payement.wallet.DTOs.RegisterUserReq;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Repo.UserRepo;
import com.payement.wallet.Service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImple implements UserService {
    private final UserRepo userRepo;
    private  final SecureRandom secureRandom = new SecureRandom();
private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean registerUser(RegisterUserReq req) {
        boolean userExistByEmail = userRepo.existsByEmail(req.getEmail());
        boolean userExistByPhoneNumber = userRepo.existsByPhoneNumber(req.getPhoneNumber());
        if (userExistByEmail || userExistByPhoneNumber) {
            return false;
        }
        log.info("this is the username: {}" ,req.getUsername());
        UserEntity user = UserEntity.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .otherNames(req.getOtherNames())
                .username(req.getUsername().trim())
                .email(req.getEmail())
                .password(req.getPassword())
                .phoneNumber(req.getPhoneNumber())
                .status(Status.UNVERIFIED)
                .build();
        userRepo.save(user);
        String otp = generateVerificationCode();
        sendOtp(otp);
        String userId = getUserId(req.getPhoneNumber()).toString();
        String key = userId + " otp";
        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(15));
        return true;
    }
    public  String generateVerificationCode() {
        log.info("building otp");
        int  otp = 1000 + secureRandom.nextInt(9000);
        return String.valueOf(otp);
    }
    public  void  sendOtp(String otp) {
        log.info("the otp to be sent is: {}" , otp);
    }
    public  Long getUserId(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber).getId();
    }
}
