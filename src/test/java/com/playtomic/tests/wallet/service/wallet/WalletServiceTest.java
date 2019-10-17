package com.playtomic.tests.wallet.service.wallet;

import com.playtomic.tests.wallet.model.entity.Wallet;
import com.playtomic.tests.wallet.model.exception.NotEnoughMoneyException;
import com.playtomic.tests.wallet.model.exception.PaymentServiceRequestException;
import com.playtomic.tests.wallet.model.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.model.repository.WalletRepository;
import com.playtomic.tests.wallet.service.gateway.PaymentServiceException;
import com.playtomic.tests.wallet.service.gateway.ThirPartyPaymentResolver;
import com.playtomic.tests.wallet.service.gateway.impl.ThirdPartyPaymentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceTest {

    @Captor
    private ArgumentCaptor<Wallet> walletArgumentCaptor;

    @Mock
    private ThirdPartyPaymentService thirdPartyPaymentServiceMock;

    @Mock
    private WalletRepository walletRepositoryMock;

    @Mock
    private ThirPartyPaymentResolver thirPartyPaymentResolverMock;

    @InjectMocks
    private WalletService walletService;

    @Test
    public void shouldReturnBalanceOfAnAccount_whenReceivingAWalletId() {
        when(walletRepositoryMock.findOne(1L)).thenReturn(new Wallet(1L, 2.56));

        Double balance = walletService.getBalance(1L);

        assertEquals(2.56, balance, 0.0);
    }

    @Test(expected = WalletNotFoundException.class)
    public void shouldThrowWalletNotFoundException_whenThereIsNoWalletWithReceivedIdInDb() {
        walletService.getBalance(1L);
    }

    @Test
    public void shouldWithdrawMoneyFromWallet_whenReceivingAWalletIdAndAnAmount() {
        when(walletRepositoryMock.findOne(1L)).thenReturn(new Wallet(1L, 1000.0));

        walletService.withdrawFromWallet(1L, 50.0);

        verify(walletRepositoryMock).save(walletArgumentCaptor.capture());
        Wallet walletCaptured = walletArgumentCaptor.getValue();
        assertEquals(950.0, walletCaptured.getBalance(), 0.0);
    }

    @Test
    public void shouldHaveBalance0_whenWithdrawingSameAmountAsBalanceInWallet() {
        when(walletRepositoryMock.findOne(1L)).thenReturn(new Wallet(1L, 50.0));

        walletService.withdrawFromWallet(1L, 50.0);

        verify(walletRepositoryMock).save(walletArgumentCaptor.capture());
        Wallet walletCaptured = walletArgumentCaptor.getValue();
        assertEquals(0.0, walletCaptured.getBalance(), 0.0);
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void shouldThrowNoEnoughMoneyAvailableException_whenTheresNotEnoughMoneyInTheWallet() {
        when(walletRepositoryMock.findOne(1L)).thenReturn(new Wallet(1L, 10.0));

        walletService.withdrawFromWallet(1L, 50.0);
    }

    @Test
    public void shouldChargeCorrectlyRequestedAmountToThirdPartyPaymentServiceIntoWallet_whenRequestingMoreThan10EurosRecharge() {
        when(walletRepositoryMock.findOne(1L)).thenReturn(new Wallet(1L, 10.0));
        when(thirPartyPaymentResolverMock.resolve(anyString())).thenReturn(thirdPartyPaymentServiceMock);

        walletService.rechargeWallet(1L, 15.0, "thirdPartyPaymentService");

       verify(walletRepositoryMock).save(walletArgumentCaptor.capture());
        Wallet walletCaptured = walletArgumentCaptor.getValue();
        assertEquals(25.0, walletCaptured.getBalance(), 0.0);
    }

    @Test(expected = PaymentServiceRequestException.class)
    public void shouldThrowPaymentServiceRequestException_whenRequestingLessThan10Euros() throws PaymentServiceException {
        when(walletRepositoryMock.findOne(1L)).thenReturn(new Wallet(1L, 10.0));
        when(thirPartyPaymentResolverMock.resolve(anyString())).thenReturn(thirdPartyPaymentServiceMock);
        doThrow(new PaymentServiceException("Test error.")).when(thirdPartyPaymentServiceMock).charge(any());

        walletService.rechargeWallet(1L, 9.0, "thirdPartyPaymentService");
    }
}