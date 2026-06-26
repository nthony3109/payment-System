package com.payement.wallet.Service.interfaces;

import com.payement.wallet.DTOs.RegisterUserReq;
import com.payement.wallet.Entity.UserEntity;

public interface UserService {
    // more are coming for login and other user related functionalities
    boolean registerUser(RegisterUserReq req);
}
