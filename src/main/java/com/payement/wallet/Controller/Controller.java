package com.payement.wallet.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class Test {

    @GetMapping("/test")
    public ResponseEntity<String> printText() {
        return ResponseEntity.ok ("hello, mr-T \n" +
                "i am running, u can start coding now");
    }
}
