package com.payement.wallet.Exceptions;

import com.payement.wallet.Enum.ErrorCode;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, Object> err = new HashMap<>();
        err.put("errorType", ErrorCode.VALIDATION_ERROR);
        err.put("time", LocalDateTime.now());

        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        err.put("details", details);
        return ResponseEntity.badRequest().body(err);

    }

    public ResponseEntity<Map<String,Object>> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, Object> err = new HashMap<>();
        err.put("errorType", ErrorCode.CONSTRAINT_VIOLATION);
        err.put("time", LocalDateTime.now());
        err.put("details", exception.getMessage());
        return ResponseEntity.badRequest().body(err);
    }

    public  ResponseEntity<Map<String,Object>>  handleAccountNotFoundException(AccountNotFoundException ex) {
        return buildErrorResponse(ErrorCode.ACCCOUNT_NOT_FOUND, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<String,Object>> handleInsufficientFundException(InsufficientFundException ex) {
        return buildErrorResponse(ErrorCode.INSUFFICIENT_FUND, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Map<String, Object>> handleInvalidPhoneNumberException(InvalidPhoneNumberException ex) {
        return buildErrorResponse(ErrorCode.INVALID_PHONE_NUMBER, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
     public ResponseEntity<Map<String, Object>> handleInvalidTransferAmount(invalidTransferAmount ex) {
         return buildErrorResponse(ErrorCode.INVALID_AMOUNT, ex.getMessage(), HttpStatus.BAD_REQUEST);
     }
    public ResponseEntity<Map<String, Object>> handleInvalidAmountException(InvalidDepositAmountException ex) {
        return buildErrorResponse(ErrorCode.INVALID_AMOUNT, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(ErrorCode code, String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorCode", code);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
