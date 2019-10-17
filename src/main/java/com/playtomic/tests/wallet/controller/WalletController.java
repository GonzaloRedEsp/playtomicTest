package com.playtomic.tests.wallet.controller;

import com.playtomic.tests.wallet.service.wallet.WalletService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

    final private Logger log = LoggerFactory.getLogger(WalletController.class);

    private WalletService walletService;

    @RequestMapping("/{id}")
    public ResponseEntity getWallet(@PathVariable Long id) {
        return new ResponseEntity<>(walletService.getBalance(id), OK);
    }

    @RequestMapping("/withdraw/{id}")
    public ResponseEntity withdrawFromWallet(@PathVariable Long id, @RequestParam Double amount) {
        walletService.withdrawFromWallet(id, amount);
        return new ResponseEntity(OK);
    }

    @RequestMapping("/recharge/{id}")
    public ResponseEntity rechargeWallet(@PathVariable Long id, @RequestParam Double amount, @RequestParam String service){
        walletService.rechargeWallet(id, amount, service);
        return new ResponseEntity(OK);
    }
}
