package com.playtomic.tests.wallet.integration;

import com.playtomic.tests.wallet.model.entity.Wallet;
import com.playtomic.tests.wallet.model.repository.WalletRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WalletIntegrationTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        walletRepository.deleteAll();
    }

    @Test
    public void shouldReturnCurrentBalance_whenLookingForAExistingWallet() throws Exception {
        Wallet wallet = walletRepository.save(new Wallet(null, 10.0));

        mockMvc.perform(get("/wallet/" + wallet.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Double.toString(10.0)));
    }

    @Test
    public void shouldReturn404_whenRequestedWalletBalanceIsNotPresent() throws Exception {
        mockMvc.perform(get("/wallet/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Wallet not found in database.")));
    }

    @Test
    public void shouldReturn200AndWithdrawMoneyFromWallet_whenThereIsEnoughBalanceAvailable() throws Exception {
        Wallet wallet = walletRepository.save(new Wallet(null, 100.0));

        mockMvc.perform(get("/wallet/withdraw/" + wallet.getId()).param("amount", "9.00"))
                .andExpect(status().isOk());

        Wallet modifiedWallet = walletRepository.findOne(wallet.getId());
        assertEquals(91.0, modifiedWallet.getBalance(), 0.0);
    }

    @Test
    public void shouldReturn404_whenWithdrawingFromNotPresentWallet() throws Exception {
        mockMvc.perform(get("/wallet/withdraw/10").param("amount", "9.00"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn406_whenWithdrawingFromAWalletWithInsuficientBalance() throws Exception {
        Wallet wallet = walletRepository.save(new Wallet(null, 8.0));

        mockMvc.perform(get("/wallet/withdraw/" + wallet.getId())
                .param("amount", "9.00"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldReturn200AndDoRechargeOnWalletWithRequestedAmount_whenRechargingWalletCorrectly() throws Exception {
        Wallet wallet = walletRepository.save(new Wallet(null, 8.0));

        mockMvc.perform(get("/wallet/recharge/" + wallet.getId())
                .param("amount", "50.00")
                .param("service", "thirdPartyPaymentService"))
                .andExpect(status().isOk());

        Wallet modifiedWallet = walletRepository.findOne(wallet.getId());
        assertEquals(58.0, modifiedWallet.getBalance(), 0.0);
    }

    @Test
    public void shouldReturn404_whenRechargingNotPresentWallet() throws Exception {
        mockMvc.perform(get("/wallet/recharge/10")
                .param("amount", "50.00")
                .param("service", "thirdPartyPaymentService"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn404_whenRequestedPaymentServiceIsNotAvailable() throws Exception {
        Wallet wallet = walletRepository.save(new Wallet(null, 8.0));

        mockMvc.perform(get("/wallet/recharge/" + wallet.getId())
                .param("amount", "50.00")
                .param("service", "errorPaymentService"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Payment service selected not available")));
    }

    @Test
    public void shouldReturn406_whenPaymentServiceReturnsAnErrorRelatedToAnInternalCheck() throws Exception {
        Wallet wallet = walletRepository.save(new Wallet(null, 8.0));

        mockMvc.perform(get("/wallet/recharge/" + wallet.getId())
                .param("amount", "8.00")
                .param("service", "thirdPartyPaymentService"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorMessage", containsString("Amount requested is lower than minimum")));
    }
}
