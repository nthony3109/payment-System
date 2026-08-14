package com.payement.wallet.Services;

import com.payement.wallet.DTOs.RegisterUserReq;
import com.payement.wallet.Entity.UserEntity;
import com.payement.wallet.Repo.UserRepo;
import com.payement.wallet.Service.EmailService;
import com.payement.wallet.Service.Implementations.UserServiceImple;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceTest {
    @InjectMocks
    UserServiceImple userService;
    @InjectMocks
    EmailService emailService;
    @Mock
    UserRepo repo;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;


    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void  testMethod() {
        log.info("building user registration request");
        RegisterUserReq user = RegisterUserReq.builder()
                .id(10L)
                .email("chi@gmail.com")
                .lastName("chi")
                .firstName("tony")
                .password("12345")
                .username("mr-t")
                .phoneNumber("07017996873")
                .build();

        when(repo.save(any(UserEntity.class))).thenReturn(UserEntity.builder().id(10L).build());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(repo.findByPhoneNumber(user.getPhoneNumber())).thenReturn(UserEntity.builder().id(10L).build());
        boolean registered = userService.registerUser(user);

        verify(repo).save(any(UserEntity.class));
        verify(redisTemplate).opsForValue().set(any(String.class), any(), any(Duration.class));

    }

    @Test
    void sendmail () {
        String mail = "chibuzoranthonyajibo@gmail.com";
        String otp = "3041";
        emailService.sendOtpByMail(mail,otp);
    }
}
