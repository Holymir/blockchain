package com.softuni.blockchain.node;


import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.Crypto;
import com.softuni.blockchain.wallet.WalletController;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class NodeController {

    private List<Transaction> pendingTransactions = new ArrayList<>();

    @Autowired
    private WalletController walletController;

    public Transaction createTransaction(Transaction transaction) {
        // 1. validate Transaction
        this.verifyTransaction(transaction.getCorePart());

        // 3. add to pending transactions
        pendingTransactions.add(transaction);

        transaction.setDateReceived(Instant.now().toEpochMilli());
        transaction.setTransactionHash("0x" + Hex.toHexString(Crypto.sha256(Utils.serialize(transaction).getBytes())));

        return transaction;
    }

    private void verifyTransaction(Transaction transaction) {
        this.walletController.verify(Utils.serialize(transaction), transaction.getSenderSignature(), transaction.getSenderPubKey());
    }

    public List<Transaction> getPendingTransactions() {
        return this.pendingTransactions;
    }
}
