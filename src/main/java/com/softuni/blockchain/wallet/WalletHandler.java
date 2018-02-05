package com.softuni.blockchain.wallet;


import com.softuni.blockchain.node.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletHandler {

    @Autowired
    private WalletController walletController;


    @PostMapping("/wallet")
    public Wallet generateWallet() {
        return this.walletController.generateWallet();
    }


    @PostMapping("/wallet/{publicKey}/address")
    public Wallet generateWallet(@PathVariable String publicKey) {
        return this.walletController.generateWallet();
    }
}
