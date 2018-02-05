package com.softuni.blockchain.node;


import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.Wallet;
import com.softuni.blockchain.wallet.WalletController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class NodeController {

    public static List<Transaction> pendingTransactions = new ArrayList<>();

    @Autowired
    private WalletController walletController;

    @Autowired
    private Node node;

    public Node getInfo() {
        return node;
    }

    public Transaction createTransaction(Transaction transaction, Wallet wallet) {
        // 1. validateTransaction
        this.validateTransaction(transaction);

        // 2. sign
        String signMessage = walletController.signMessage(Utils.serialize(transaction), wallet.getPrivateKey());
        transaction.setSenderPubKey(wallet.getPublicKey());
        transaction.setSenderSignature(signMessage);

        // 3. add to pending transactions
        pendingTransactions.add(transaction);

        transaction.setDateReceived(Instant.now().toEpochMilli());

        // 4. pass to pull for mining

        return transaction;
    }

    private void validateTransaction(Transaction transaction) {

    }
}
