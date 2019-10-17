package com.playtomic.tests.wallet.service.wallet;

import com.playtomic.tests.wallet.model.entity.Wallet;
import com.playtomic.tests.wallet.model.exception.NotEnoughMoneyException;
import com.playtomic.tests.wallet.model.exception.PaymentServiceRequestException;
import com.playtomic.tests.wallet.model.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.model.repository.WalletRepository;
import com.playtomic.tests.wallet.service.gateway.PaymentService;
import com.playtomic.tests.wallet.service.gateway.PaymentServiceException;
import com.playtomic.tests.wallet.service.gateway.ThirPartyPaymentResolver;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class WalletService {

    private WalletRepository walletRepository;
    private ThirPartyPaymentResolver thirPartyPaymentResolver;

    @Cacheable("wallets")
    public Double getBalance(Long id) {
        Wallet walletRetrieved = walletRepository.findOne(id);

        if (walletRetrieved == null) {
            throw new WalletNotFoundException();
        }

        return walletRetrieved.getBalance();
    }

    public void withdrawFromWallet(Long id, Double amount) {
        Wallet walletRetrieved = walletRepository.findOne(id);

        if (walletRetrieved == null) {
            throw new WalletNotFoundException();
        }
        Double currentBalance = walletRetrieved.getBalance();
        if(currentBalance.compareTo(amount) < 0) {
            throw new NotEnoughMoneyException();
        }
        walletRetrieved.setBalance(currentBalance - amount);

        walletRepository.save(walletRetrieved);
    }

    public void rechargeWallet(Long id, Double amount, String thirdPartyPaymentService) {
        Wallet walletRetrieved = walletRepository.findOne(id);
        if (walletRetrieved == null) {
            throw new WalletNotFoundException();
        }

        Double currentBalance = walletRetrieved.getBalance();
        PaymentService paymentService = thirPartyPaymentResolver.resolve(thirdPartyPaymentService);
        try {
            paymentService.charge(BigDecimal.valueOf(amount));
        } catch (PaymentServiceException e) {
            throw new PaymentServiceRequestException(e.getMessage());
        }
        walletRetrieved.setBalance(currentBalance + amount);
        walletRepository.save(walletRetrieved);
    }
}
