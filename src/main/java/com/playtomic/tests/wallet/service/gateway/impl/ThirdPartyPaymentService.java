package com.playtomic.tests.wallet.service.gateway.impl;

import com.playtomic.tests.wallet.service.gateway.PaymentService;
import com.playtomic.tests.wallet.service.gateway.PaymentServiceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * A real implementation would call to a third party's payment service (such as Stripe, Paypal, Redsys...).
 *
 * This is a dummy implementation which throws an error when trying to change less than 10€.
 */
@Service
public class ThirdPartyPaymentService implements PaymentService {
    private BigDecimal threshold = new BigDecimal(10);

    @Override
    public boolean canHandle(String paymentService) {
        return paymentService.equals("thirdPartyPaymentService");
    }

    @Override
    public void charge(BigDecimal amount) throws PaymentServiceException {
        if (amount.compareTo(threshold) < 0) {
            throw new PaymentServiceException("Amount requested is lower than minimum (10 euros).");
        }
    }
}
