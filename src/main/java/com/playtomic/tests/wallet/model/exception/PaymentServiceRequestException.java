package com.playtomic.tests.wallet.model.exception;

public class PaymentServiceRequestException extends RuntimeException{
    public PaymentServiceRequestException(String message) {
        super(message);
    }
}
