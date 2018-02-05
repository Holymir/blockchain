package com.softuni.blockchain.wallet;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletHandler {

    @Autowired
    private WalletController walletController;


    @GetMapping("/wallet")
    public Wallet generateWallet() {
       return this.walletController.generateWallet();
    }
}
