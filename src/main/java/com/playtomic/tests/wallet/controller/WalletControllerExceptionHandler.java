package com.playtomic.tests.wallet.controller;

import com.playtomic.tests.wallet.model.dto.WalletResponse;
import com.playtomic.tests.wallet.model.exception.NoPaymentServiceFoundException;
import com.playtomic.tests.wallet.model.exception.NotEnoughMoneyException;
import com.playtomic.tests.wallet.model.exception.PaymentServiceRequestException;
import com.playtomic.tests.wallet.model.exception.WalletNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class WalletControllerExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<Object> handleWalletNotFoundException() {
        return new ResponseEntity<>(new WalletResponse(NOT_FOUND.toString(), "Wallet not found in database."), NOT_FOUND);
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<Object> handleNotEnoughMoneyException() {
        return new ResponseEntity<>(new WalletResponse(NOT_ACCEPTABLE.toString(), "Not enough money in your wallet."), NOT_ACCEPTABLE);
    }

    @ExceptionHandler(PaymentServiceRequestException.class)
    public ResponseEntity<Object> handlePaymentServiceRequestException(PaymentServiceRequestException exception) {
        return new ResponseEntity<>(new WalletResponse(NOT_ACCEPTABLE.toString(), exception.getMessage()), NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NoPaymentServiceFoundException.class)
    public ResponseEntity<Object> handleNoPaymentServiceFoundException() {
        return new ResponseEntity<>(new WalletResponse(NOT_FOUND.toString(), "Payment service selected not available."), NOT_FOUND);
    }
}
