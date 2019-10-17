package com.playtomic.tests.wallet.service.gateway;

import com.playtomic.tests.wallet.model.exception.NoPaymentServiceFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ThirPartyPaymentResolver {

    private List<PaymentService> paymentServices;

    public PaymentService resolve (String paymentService){
        return paymentServices.stream()
                .filter(service -> service.canHandle(paymentService))
                .findFirst()
                .orElseThrow(NoPaymentServiceFoundException::new);
    }

}
