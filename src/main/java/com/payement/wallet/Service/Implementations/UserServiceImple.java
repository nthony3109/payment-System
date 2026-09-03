package com.payement.wallet.Service.Implementations;

import com.payement.wallet.DTOs.RegisterUserReq;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Enum.Status;
import com.payement.wallet.Exceptions.AccountNotFoundException;
import com.payement.wallet.Repo.UserRepo;
import com.payement.wallet.Service.EmailService;
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
private  final EmailService emailService;

    @Override
    public boolean registerUser(RegisterUserReq req) {
        boolean userExistByEmail = userRepo.existsByEmail(req.getEmail());
        boolean userExistByPhoneNumber = userRepo.existsByPhoneNumber(req.getPhoneNumber());
        if (userExistByEmail || userExistByPhoneNumber) {
            return false;
        }
        log.info("this is the username: {}" ,req.getUsername());
        //build the user details
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
        //to get generate otp
        String otp = generateVerificationCode();
        //to send otp by sms/email
        sendOtp(req.getEmail(),otp);
        //to get userId for redis otp caching
        String userId = getUserId(req.getPhoneNumber()).toString();

        String key = userId + " otp";
        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(15));
        return true;
    }

    @Override
    public  String generateVerificationCode() {
        log.info("building otp");
        int  otp = 1000 + secureRandom.nextInt(9000);
        return String.valueOf(otp);
    }

    @Override
    public  void  sendOtp( String mail, String otp) {
        log.info("the otp to be sent  to {} is: {} " , mail, otp);
        emailService.sendOtpByMail(mail,otp);

    }

    @Override
    public  Long getUserId(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber).getId();
    }

    @Override
    public  String verifyOtp(String userId, String otp) {
        String code = redisTemplate.opsForValue().get(userId + " otp");
        if (code == null) {
            return "OTP has expired.";
        }
        if (!code.equals(otp)) {
            return "OTP is incorrect.";
        }
        Long id =  Long.parseLong(userId);
        setUserActive(id);
        return "OTP is valid.";
    }

    @Override
    public  void  setUserActive(Long id ) {
        UserEntity user = userRepo.findById(id)
                        .orElseThrow(
                                () -> new AccountNotFoundException("account not found")
                        );
        user.setStatus(Status.VERIFIED);
        userRepo.save(user);

    }

    public  String getUserEmail(String phoneNUmber) {
        return userRepo.findByPhoneNumber(phoneNUmber).getEmail();
    }
}
