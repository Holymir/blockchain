package com.softuni.blockchain.wallet;


import com.softuni.blockchain.node.NodeController;
import com.softuni.blockchain.node.RequestTransactionDTO;
import com.softuni.blockchain.node.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletHandler {

    @Autowired
    private WalletController walletController;


    @PostMapping("/")
    public Wallet generateWallet() {
        return this.walletController.generateWallet();
    }

    @PostMapping("/{publicKey}/address")
    public Wallet generateWallet(@PathVariable String publicKey) {
        return this.walletController.generateWallet();
    }

    @PostMapping("/transactions/sign")
    public Transaction createTransaction(@RequestBody RequestTransactionDTO transactionDTO) {
        return this.walletController.createTransaction(transactionDTO.getTransaction(), transactionDTO.getWallet());
    }
}
