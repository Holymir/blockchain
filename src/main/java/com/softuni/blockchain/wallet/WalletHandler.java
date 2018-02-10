package com.softuni.blockchain.wallet;


import com.softuni.blockchain.node.model.RequestTransactionDTO;
import com.softuni.blockchain.node.model.Transaction;
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

    @PostMapping("/transactions/sign")
    public Transaction sign(@RequestBody RequestTransactionDTO transactionDTO) {
        return this.walletController.signTransaction(transactionDTO.getTransaction(), transactionDTO.getWallet());
    }
}
