package com.playtomic.tests.wallet.service.gateway;

import java.math.BigDecimal;

public interface PaymentService {
    boolean canHandle(String paymentService);
    void charge(BigDecimal amount) throws PaymentServiceException;
}
