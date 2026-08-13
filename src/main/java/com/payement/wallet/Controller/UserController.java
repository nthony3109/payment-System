package com.payement.wallet.Controller;

import com.payement.wallet.DTOs.RegisterUserReq;
import com.payement.wallet.DTOs.VerifyOtpReq;
import com.payement.wallet.Service.Implementations.UserServiceImple;
import com.payement.wallet.Service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
   private  final UserService userService;


    @GetMapping("/test")
    @Operation(summary = "Test endpoint to verify the application is running")
    public ResponseEntity<String> printText() {
        return ResponseEntity.ok ("hello, mr-T \n" +
                "i am running, u can start coding now");
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Endpoint to register a new user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserReq req) {
         boolean userRegistered = userService.registerUser(req);
         if (!userRegistered) {
             return ResponseEntity.badRequest().body("UserEntity registration failed");
         }
        return ResponseEntity.ok("registration successful");
    }

    @PostMapping("verify")
    @Operation(summary = "Endpoint to verify a user")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyOtpReq req) {
         String userId = req.getUserId();
         String otp = String.valueOf(req.getUserId());
         userService.verifyOtp(userId,otp);

        return ResponseEntity.ok("verification successful ");
    }

}
