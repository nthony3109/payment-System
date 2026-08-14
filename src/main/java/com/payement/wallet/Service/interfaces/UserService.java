package com.payement.wallet.Service.interfaces;

import com.payement.wallet.DTOs.RegisterUserReq;
import com.payement.wallet.Entity.UserEntity;

public interface UserService {
    // more are coming for login and other user related functionalities
    boolean registerUser(RegisterUserReq req);
    String verifyOtp(String userId, String otp);
    String generateVerificationCode();
    void  sendOtp(String mail,String otp);
    Long getUserId(String phoneNumber);
    void  setUserActive(Long id );

}
